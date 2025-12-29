# multi-module-springboot-boilerplate
multi-module-springboot-boilerplate

1. Hexagonal Architecure.
```
       ┌──────────┐
       │   API    │  ← HTTP, CLI, gRPC
       └────┬─────┘
            ↓
       ┌──────────┐
       │Application│
       └────┬─────┘
            ↓
       ┌──────────┐
       │  Domain  │
       └────┬─────┘
            ↑
       ┌──────────┐
       │ Infra    │  ← DB, MQ, REST
       └──────────┘
```
```
bootstrap-spring
 └── depends on infra only

infra
 ├── depends on application
 └── depends on domain

api
 └── depends on application
 ```

2️⃣ What is Application (in Hexagonal / Clean Architecture)?
    Application = Use-case layer
    It answers the question:

    “What can this system do?”

    Responsibilities
        - Orchestrates business use cases
        - Coordinates domain objects
        - Defines ports (interfaces) for external systems
        - Controls transactions and consistency

Has zero knowledge of frameworks

Example responsibilities
    - Create something
    - Process something
    - Approve / reject something
    - Fetch something in a business-meaningful way

What lives here
```
application
 ├── usecase
 │    └── CreateXxxUseCase
 ├── port
 │    ├── InboundPort
 │    └── OutboundPort
 └── service
      └── XxxApplicationService
```
What it MUST NOT contain

❌ @SpringBootApplication
❌ Controllers
❌ Repositories implementations
❌ Framework annotations

2.1 Application layer is too thin (currently empty shell)

    Your application module is almost empty.
    That’s fine for now, but the next code must go here, not in api or infra.
    What should live in application
        - Use cases
        - Ports (interfaces)
        - Transaction boundaries
        - Orchestration logic

If you skip this, you’ll slide into controller-driven logic (very common mistake).

❌ 3️⃣ API module risks becoming a “smart controller”

Your controllers are currently fine, but:

⚠️ If you add:
    - business rules
    - decision logic
    - database calls
    - into controllers → architecture breaks.

Controllers must:
    - validate input
    - call application use case
    - map output

Nothing more.

3️⃣ What is bootstrap-spring (Why it Exists)
    - bootstrap-spring = Runtime entry point

It answers the question:
    - “How do we start this system?”

Responsibilities
    - Contains main() method
    - Boots Spring
    - Wires adapters together
    - Starts embedded server
    - Loads configuration

What lives here
```
bootstrap-spring
 ├── SpringDemoApplication.java
 └── config
     └── AppConfig.java
```
What it MUST NOT contain

❌ Business logic
❌ Use cases
❌ Domain rules

4️⃣ Analogy (Very Important)
🎮 Game Analogy
    - Concept	Real Life Equivalent
    - Domain	Game rules
    - Application	Game flow (turns, win conditions)
    - API	Controller / Keyboard
    - Infrastructure	Graphics / Sound / Storage
    - Bootstrap-spring	Game launcher (.exe)

The launcher is not the game logic.

5️⃣ Mapping This to YOUR Project

Your current structure:
```
springdemo-root
├── domain            ← WHAT the business is
├── application       ← WHAT the system can do
├── api               ← HOW users talk to it
├── infra             ← HOW system talks outward
└── bootstrap-spring  ← HOW the system starts
```
Concrete meaning in your repo
    - Module	        Purpose
    - domain	        Business rules, entities
    - application	    Use cases, orchestration
    - api	            REST controllers
    - infra	            DB / external implementations
    - bootstrap-spring  Spring Boot entry point
```
bootstrap-spring
 └── infra
       └── application
             └── domain
api
 └── application
       └── domain
```
🔚 Final Takeaway

Application = brain

Domain = rules

API = mouth

Infra = hands

Bootstrap-spring = body switch-on button

6️⃣ How Spring MVC Fits Into Hexagonal

This is critical 👇

Spring MVC is just an adapter

In your project:
```
api (adapter)
 └── @RestController   ← Spring MVC lives here
      ↓
application
      ↓
domain
```

So:

❌ Hexagonal does NOT replace MVC

✅ It contains MVC at the edges

7️⃣ MVC vs Hexagonal Flow Comparison
```
MVC Flow
HTTP → Controller → Service → Repository → DB
```
```
Hexagonal Flow
HTTP → API Adapter → Use Case → Domain
                         ↓
                  Outbound Port
                         ↓
                      Infra
```

🚫 Anti-Patterns to Avoid (Very Important)

The following practices break hexagonal architecture and should be avoided.

❌ 1. Business Logic Inside Controllers

Bad
```
@RestController
public class OrderController {

    @PostMapping("/orders")
    public void create(@RequestBody OrderDto dto) {
        if (dto.amount() > 10000) {
            // business rule here ❌
        }
    }
}
```

Why it’s wrong

Business rules become HTTP-dependent

Impossible to reuse logic outside REST

Hard to test without Spring

✅ Correct

Controllers only map requests → use cases

❌ 2. Domain Depending on Spring or JPA

Bad
```
@Entity
public class Order {
    @Autowired
    DiscountService service; // ❌
}
```

Why it’s wrong

Domain becomes framework-coupled

Breaks portability and testability

✅ Correct

Domain must be pure Java

No Spring, no annotations, no frameworks

❌ 3. Application Layer Calling Infrastructure Directly

Bad
```
@Service
public class CreateOrderUseCase {

    private final JpaOrderRepository repo; // ❌ concrete class
}
```

Why it’s wrong

Hard dependency on infrastructure

Violates dependency inversion

✅ Correct
```
public class CreateOrderUseCase {

    private final OrderRepositoryPort repo; // ✅ interface
}
```
❌ 4. “God Service” Pattern (MVC Leftovers)

Bad
```
@Service
public class OrderService {
    // validation
    // persistence
    // external API calls
    // mapping
    // calculations
}
```

Why it’s wrong

Becomes unmaintainable

Hidden coupling

Difficult to test

✅ Correct

One use case = one responsibility

Delegate to domain objects

❌ 5. Treating Application Layer as a Dumping Ground

Bad
```
application
 ├── utils
 ├── helpers
 ├── common
```

Why it’s wrong

Indicates missing domain modeling

Leads to anemic domain

✅ Correct
```
application
 ├── usecase
 ├── port
 └── service
```
❌ 6. Depending “Upward” in the Module Graph

Never allowed
```
domain → application ❌
application → api ❌
application → infra ❌
```

Correct dependency direction
```
api → application → domain
infra → application → domain
```

Dependencies must always point inward.

✅ Summary Rules for Contributors

    - Controllers are thin

    - Use cases orchestrate, domain decides

    - Domain is framework-free

    - Infrastructure implements ports

    - Dependency direction is sacred

    - One-Line Rule (Put This in Your Head)

    - If removing Spring breaks your business logic, the architecture is wrong.
