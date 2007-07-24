(defrule fipa-cancel
	"Fires for any cancel performative that matches an existing message-rule-pairing in its message-content. The rule found is then removed."
	(agent-cancel-result
		(initiator ?agent)
		(performative ?performative)
		(messageContent ?messageContent)
	)
	(agent-message-rule-pairing
		(message ?message)
		(ruleName ?ruleName)
	)
	(test (SL-message-compare ?messageContent (fact-slot-value ?message "content")))
	
	=>
	
	(undefrule ?ruleName)
)