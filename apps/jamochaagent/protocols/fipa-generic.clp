(defrule fipa-cancel
	"Fires for any cancel performative that matches an existing message-rule-pairing in its message-content. The rule found is then removed."
	(agent-cancel-result
		(message ?cancelMessage)
		(initiator ?agent)
		(performative ?performative)
		(messageContent ?messageContent)
	)
	(agent-message-rule-pairing
		(message ?ruleMessage)
		(ruleName ?ruleName)
	)
	;(test (SL-message-compare ?messageContent (fact-slot-value ?ruleMessage "content")))
	(test
		(eq
			(fact-slot-value ?cancelMessage "conversation-id")
			(fact-slot-value ?ruleMessage "conversation-id")
		)
	)
	(test
		(eq ?agent (fact-slot-value ?ruleMessage "sender"))
	)
	=>
	
	; TODO: maybe we should send an confirm or inform message here??
	
	(undefrule ?ruleName)
)