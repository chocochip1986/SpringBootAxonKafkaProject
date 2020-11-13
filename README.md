#**Spring Boot Axon Kafka Project**
---


#**How to run this**
---

###To create an Order with Order Transactions, run a Post call with the following payload (example)
```
Post call to   
localhost:9998/api/v1/shootme/order
{
	"orderName": "FirstOrder",
    "price": "7155",
    "orderTransactions": [
        {"amount": "121.00"},
        {"amount": "14398.00"}
    ]
}
```

###Update an Order with Order Transactions, run a Put call with the following payload
The prerequisite here is that you need to find out the Aggregate Identifier of the Aggregate Instance and its Aggregate Members you want to update
```
Put call to   
localhost:9998/api/v1/shootme/order
{
    "uuid": "1d257748-a8f9-4429-8fce-0595ad7ebd54",
	"orderName": "SecondOrder",
    "price": "109",
    "orderTransactions": [
        {"uuid": "8005b804-a6c7-463c-8c8d-7eb442821ae0","amount": 12131},
        {"uuid": "e41cb093-1ed6-449c-86d2-f702d4a9ac55","amount": 12.434}
    ]
}
```

###Display an Order Aggregate and it's corresponding Aggregate Members
You need to know the Aggregate Identifier of the Aggregate Instance you want to query
```
Get call to   
localhost:9998/api/v1/shootme/order/{Aggregate Identifier of the Order Aggregate}
```

###Dislay a history of the Order Aggregate
You need to know the Aggregate Identifier of the Aggregate Instance that you wish to query
```
Get call to   
localhost:9998/api/v1/shootme/order/list-events/{Aggregate Identifier of the Order Aggregate}
```

###Create a new Aggregate Member in an existing Aggregate
i.e. Inserting a new Order Transaction
The orderUuid is Aggregate Identifier of the Order Aggregate instance you want to insert a new Order Transaction.
At code level, Axon does not allow you to annotate a constructor of the Aggregate Member class with Command Handler.
You need to annotate a method in the Aggregate Root to create this new instance of Aggregate Member 
```
Post Call to:   
http://localhost:9998/api/v1/shootme/order/transaction
{
    "orderUuid": "06863809-1194-4d54-b8df-10bc763cee63",
    "amount": "93"
}
```

###Update an existing Aggregate Member in an existing Aggregate
```
Put Call to   
http://localhost:9998/api/v1/shootme/order/transaction
{
    "orderUuid": "06863809-1194-4d54-b8df-10bc763cee63",
    "orderTransactionUuid": "3033c792-e071-4837-973b-f390615409b1",
	"amount": "100.99"
}
```

#**Explanation of the various components**
---
* **AxonConfig** 
    - There's plenty of shit in here. I won't be covering the basic ones.
    * How does Axon consume messages externally published?
        * ```SubscribableKafkaMessageSource``` - This bean is required if you want Axon to consume messages that are published outside of Axon.
    * How does Axon publish messages for Kafka consumers external to Axon to eat?
        * When events are being published to an Event Bus/Event Store, Axon can forward them to a Kafka topic via the ```KafkaPublisher```
        * ```ProducerFactory``` - Based on this bean's configuration, Axon will instantiate Producers to publish events to a Topic. Kafka also has its own ProducerFactory library so be careful of where you import yours. You need the one from Axon.
        * ```KafkaPublisher``` - This bean is required to push messages to a Kafka Topic. You'll need to declare a ```ProducerFactory``` bean.
        * ```KafkaEventPublisher``` - Essentially, I think this bean operates like an event handler which will push the events down to the ```KafkaPublisher```. That's why you need to set a KafkaPublisher in the bean.
    * Where are the events stored?
        * You need an implementation of the ```EventStorageEngine``` class. In this project, I use the In-Memory Storage Engine (```InMemoryEventStorageEngine```) for simple testing. There are other persistent implementations of it.
        * If you are not using Axon Server, you'll need to declare a ```EmbeddedEventStore``` bean. This bean houses the Storage Engine. 
    * How then should I query and retrieve the events of the Aggregate?
        * It isn't encouraged to ```Autowire``` and read directly from either the ```EventStorageEngine``` or the ```EmbeddedEventStore```. You will need to create a ```EventSourcingRepository``` bean. Through this bean, you get your events. 
        
#**Questions I have yet to find answers**
* How does deletion of Aggregate Instances work?
* Can I display a list of unique Aggregate Instances?
