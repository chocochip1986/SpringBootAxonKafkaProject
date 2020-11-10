How to run this
=================

To create an Order, run a Post call with the following payload (example)
```
{
	"orderName": "FirstOrder",
    "price": "7155",
    "orderTransactions": [
        {"amount": "121.00"},
        {"amount": "14398.00"}
    ]
}
```

You can find out the uuid of the order via console log

