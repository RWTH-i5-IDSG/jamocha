(defrule fipa-cancel
	"Fires for any cancel performative whose canceled message id is the same as the one of initiating a request. The rule found is then removed."
	(agent-cancel-result
		(cancelMessage ?cancelMessage&:(neq ?cancelMessage NIL))
	)
	(agent-message-rule-pairing
		(message ?ruleMessage)
		(ruleName ?ruleName)
	)
	(test
		(eq
			(fact-slot-value ?cancelMessage "conversation-id")
			(fact-slot-value ?ruleMessage "conversation-id")
		)
	)
	(test
		(eq
			(fact-slot-value ?cancelMessage "sender")
			(fact-slot-value ?ruleMessage "sender")
		)
	)
	
	=>
	
	; TODO: maybe we should send an confirm or inform message here??
	
	(undefrule ?ruleName)
)