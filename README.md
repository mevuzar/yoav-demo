                                    
                                    
Order Of Operations:

1. In domain.contract.scripts create Scenarios.scala and write down scenarios - pay attention to _Operation_ and _Entity_/_Value Object_ naming as these should serve you when creating 
   your contexts algebra.
2. Create bounded contexts(Accounts, Users, Products, Recommendations)
3. For each context create the algebra - _Aggregate_(_Entities_/_Value Objects_) _Service_(_Operations_) which extends _Aggregate_). 
4. In domain.contract.scripts implement both the aggregate and service in the simplest way possible.
5. In Scenarios.scala implement a operation composition per scenario(from item #1).


When writing your aggregate and service, there are 2 nice flavours I can recommend(each one with its pros and cons):
1. Enforced tupled signature:
     In the aggregate: 
      
     `type CreateUserRequest = {
        val name: String
        val age: Int
     }`
     
     In the contractual service:
     
     `def createUser: Operation[CreateUserRequest, UserId]`
     
     When extending the contractual service, using the automatic implement method feature of your IDE, you will get:
     `def createUser(request: {val name: String; val age: Int}): UserId = ???`
      
      `case class CreateUserDTO(name: String, age: Int)` -> notice that the members name should match precisley the ones in the 
       aggregate!
      
      `createUser(G("Yoav", 36))`