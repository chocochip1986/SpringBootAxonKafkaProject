#**Spring Boot Axon Kafka Project**
---


#**How to run this**
---

###To create an Order with Order Transactions, run a Post call with the following payload (example)
```
Post call to localhost:9998/api/v1/shootme/order
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
The prerequiste here is that you need to find out what is the uuid of the Aggregate and it Aggregate Members you want to update
```
Put call to localhost:9998/api/v1/shootme/order
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
You need to know the UUID of the Aggregate you want to query
```
localhost:9998/api/v1/shootme/order/06863809-1194-4d54-b8df-10bc763cee63
```


#**Explanation of the various components**
---
 
