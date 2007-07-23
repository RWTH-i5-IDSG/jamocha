; ===================================================
; Definition of rules that handle the protocol
; fipa-request for all possible performatives.
; ===================================================

(defrule fipa-request-error
	"Fires for agent-evaluation-error facts with protocol request and performative request."
	?message <- (agent-message
		(protocol "fipa-request")
		(performative "request")
	)
	(agent-evaluation-error
		(message ?message)
		(error ?error)
	)
	
	=>
	
	(bind ?receivers (prepare-receivers ?message))
	
	(assert 
		(agent-message
			(receivers ?receivers)
			(performative "refuse")
			(content (str-cat (fact-slot-value ?message "content") crlf "\"" ?error "\""))
			(language (fact-slot-value ?message "language"))
			(encoding (fact-slot-value ?message "encoding"))
			(ontology (fact-slot-value ?message "ontology"))
			(protocol (fact-slot-value ?message "protocol"))
			(conversation-id (fact-slot-value ?message "conversation-id"))
			(in-reply-to (fact-slot-value ?message "reply-with"))
			(reply-with "")
			(reply-by 0)
			(timestamp (datetime2timestamp (now)))
			(incoming FALSE)
		)
	)
)

(defrule fipa-request
	"Fires for agent-request-result facts with protocol request and performative request."
	?message <- (agent-message
		(protocol "fipa-request")
		(performative "request")
	)
	(agent-request-result
		(message ?message)
		(result ?result)
	)
	
	=>
	
	(send-agree ?message "")
	
	(bind ?receivers (prepare-receivers ?message))
	
	(bind ?resultContent
		(str-cat (fact-slot-value ?message "content") (clips2sl ?result))
	)
		
	(assert
		(agent-message
			(receivers ?receivers)
			(performative "inform")
			(content ?resultContent)
			(language (fact-slot-value ?message "language"))
			(encoding (fact-slot-value ?message "encoding"))
			(ontology (fact-slot-value ?message "ontology"))
			(protocol (fact-slot-value ?message "protocol"))
			(conversation-id (fact-slot-value ?message "conversation-id"))
			(in-reply-to (fact-slot-value ?message "reply-with"))
			(reply-with "")
			(reply-by 0)
			(timestamp (datetime2timestamp (now)))
			(incoming FALSE)
		)
	)
)


(defrule fipa-cancel
	"Does cancel ..."
	(agent-cancel
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