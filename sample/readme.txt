/*

customer
	send(NewOrder -> [barsita])

	on PaymentDue
		reply(SubmitPayment)
	on DrinkReady
		end.

cashier
	on NewOrder
		Publish(PrepareDrink)
		Reply(PaymentDue)

	on SubmitPayment
		Publish(PaymentComplete)

barista
	on PrepareDrink
		publish(DrinkReady)
	on PaymentComplete
		publish(DrinkReady)

 */