# spring-domain-event-practice

## 💡 학습 목표
이 프로젝트는 Spring Data의 AbstractAggregateRoot를 활용하여 도메인 이벤트를 발행하는 전체 과정을 학습하고 기록하기 위한 예제입니다. 다음의 핵심 흐름을 파악하는 것을 목표로 합니다.

1. 애그리거트 루트(Order)에서 도메인 이벤트(OrderPaidEvent)를 등록하는 방법 
2. 리포지토리의 save()가 호출될 때 이벤트가 발행되는 과정 (EventPublishingRepositoryProxyPostProcessor의 역할)
3. 발행된 이벤트를 별도의 핸들러에서 구독하여 처리하는 방법

## ✅ 요구사항
- 사용자가 상품을 주문하면, 주문이 생성된다.
- 주문에 대한 결제가 성공적으로 완료되면, 주문의 상태는 '결제 완료'로 변경되고, 해당 상품의 재고를 주문 수량만큼 감소시켜야 한다.
- 만약 주문하려는 상품의 재고가 요청된 수량보다 부족할 경우, 주문은 실패한다.

## ⚠️ 중요: save() 호출이 필요한 이유
- `AbstractAggregateRoot`를 사용한 엔티티는 JPA의 더티 체킹(Dirty Checking)을 통한 상태 변경만으로는 이벤트가 발행되지 않음. 
- 반드시 리포지토리의 `save()` 메서드를 명시적으로 호출해야 등록된 이벤트들이 발행됨.

## ⚙️ 주요 컴포넌트 분석
### 1. AbstractAggregateRoot
- Spring Data JPA에서 제공하는 추상 클래스
- 도메인 이벤트를 엔티티에 쉽게 추가할 수 있도록 도와줌
- `registerEvent()` 메서드를 통해 도메인 이벤트를 등록할 수 있음
- 엔티티가 save() 될 때, 등록된 도메인 이벤트가 자동으로 발행

### 2. EventPublishingRepositoryProxyPostProcessor
- `AbstractAggregateRoot`를 상속한 엔티티의 도메인 이벤트를 발행 처리하는 **핵심 컴포넌트**
- 리포지토리를 프록시로 감싸서, save와 같은 메서드가 호출될 때 이벤트를 발행하는 인터셉터 추가

#### postProcess()
- `EventPublishingRepositoryProxyPostProcessor`가 리포지토리 프록시를 생성할 때 호출

```java
@Override
public void postProcess(ProxyFactory factory, RepositoryInformation repositoryInformation) {

    // 도메인 타입(애그리거트 루트)에 @DomainEvents 또는 @AfterDomainEventPublication 어노테이션이 붙은 메서드가 있는지 검사(AbstractAggregateRoot를 상속받으면 자동으로 붙음)
    EventPublishingMethod method = EventPublishingMethod.of(repositoryInformation.getDomainType());

    if (method == null) {
        return;
    }

    // 이벤트 발행이 필요한 리포지토리라고 판단될 경우 프록시에 EventPublishingMethodInterceptor 추가
    factory.addAdvice(new EventPublishingMethodInterceptor(method, publisher));
}
```

#### invoke()
- `postProcess`에서 추가된 `EventPublishingMethodInterceptor`의 핵심 메서드
- 리포지토리의 메서드가 호출될 때마다 이 메서드가 먼저 실행됨.

```java
@Override
@Nullable
public Object invoke(MethodInvocation invocation) throws Throwable {

    // 실제 메서드(ex. JpaRepository의 save) 호출
    Object result = invocation.proceed();

    // 현재 호출된 메서드가 이벤트 발행 대상 메서드가 아니라면 추가 작업 없이 즉시 원본 결과 반환 
    if (!isEventPublishingMethod(invocation.getMethod())) {
        return result;
	}

    // 메서드에 전달된 인자(저장된 애그리거트)로부터 도메인 이벤트를 찾아 발행
    Iterable<?> arguments = asIterable(invocation.getArguments()[0], invocation.getMethod());
    
    eventMethod.publishEventsFrom(arguments, publisher);

    return result;
}
```

#### publishEventsFrom()
- 애그리거트에서 도메인 이벤트를 수집하고 발행하는 실제 로직이 구현된 메서드

```java
public void publishEventsFrom(@Nullable Iterable<?> aggregates, ApplicationEventPublisher publisher) {

    if (aggregates == null) {
        return;
	}

    for (Object aggregateRoot : aggregates) {

        if (!type.isInstance(aggregateRoot)) {
            continue;
        }

        // 리플렉션을 사용해 @DomainEvents 어노테이션이 붙은 메서드 호출하여 도메인 이벤트 목록 수집
        var events = asCollection(ReflectionUtils.invokeMethod(publishingMethod, aggregateRoot));

        // 가져온 이벤트 목록을 순회하며 하나씩 발행
        for (Object event : events) {
            publisher.publishEvent(event);
        }

        // 이벤트 발행 중에 리스너가 애그리거트의 이벤트 목록을 변경하지 않았는지 검사
        var postPublication = asCollection(ReflectionUtils.invokeMethod(publishingMethod, aggregateRoot));

        if (events.size() != postPublication.size()) {

            postPublication.removeAll(events);

            throw new IllegalStateException(ILLEGAL_MODIFCATION.formatted(postPublication));
        }

        // @AfterDomainEventPublication 어노테이션이 붙은 메서드 호출하여 도메인 이벤트 목록 초기화
        if (clearingMethod != null) {
            ReflectionUtils.invokeMethod(clearingMethod, aggregateRoot);
        }
    }
}
```

## ✍️ 학습 회고
- 새로운 개념 학습을 위해 직접 예제를 만드는 방식을 시도, 단순히 사용하는 것보다 바운디드 컨텍스트를 표현하고자 했던 의도때문에 생각보다 시간이 더 걸림.
- 디버깅을 통해 프레임워크의 내부 동작을 학습하는 과정에서, 효율적으로 중단점을 설정하고 상태를 추적하는 디버깅 스킬의 부족함을 느낌. 
- 기술을 더 깊이 이해하기 위한 디버깅 능력 향상의 필요성을 느낌
