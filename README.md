# rabbitmq-abstractor
Abstractor for rabbitmq connections

How to use it?
- 
You should simply import the main packages("com.natal.rabbitmq.abstractor") 
into your project, and then create a consumer (extending from AmqpConsumer) or a publisher (extending from AmqpPublisher)
class inside your own packages. After implementing the methods, just define the exchange/queue name and it's good to go!
Run your application to check the connections being stablished.