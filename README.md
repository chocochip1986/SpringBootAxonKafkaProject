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
 
