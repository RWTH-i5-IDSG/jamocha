; ===================================================
; Definition of rules that handle the protocol
; fipa-query for all possible performatives.
; ===================================================

(defrule fipa-query-queryIf-error
	"Fires for agent-message-evaluation-result facts with protocol query and performative query-if."
	?message <- (agent-message
		(protocol "fipa-query")
		(performative "query-if")
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

(defrule fipa-query-queryIf
	"Fires for agent-message-evaluation-result facts with protocol query and performative query-if."
	?message <- (agent-message
		(protocol "fipa-query")
		(performative "query-ref")
	)
	(agent-queryIf-result
		(message ?message)
		(result ?result)
	)
	
	=>
	
	(send-agree ?message "")
	
	(bind ?receivers (prepare-receivers ?message))
	
	(bind ?resultContent (fact-slot-value ?message "content"))
	(if
		(eq ?result FALSE)
	then
		(bind ?resultContent
			(str-cat 
				"((not "
				(strip-braces ?resultContent)
				"))"
			)
		)
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


(defrule fipa-query-queryRef-error 
	"Fires whenever an error in a query-ref perfomative occured."
	?message <- (agent-message
		(protocol "fipa-query")
		(performative "query-ref")
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

(defrule fipa-query-queryRef-all
	"Fires for agent-message-evaluation-result facts with protocol query and performative query-ref."
	?message <- (agent-message
		(protocol "fipa-query")
		(performative "query-ref")
	)
	(agent-queryRef-result
		(message ?message)
		(refOp "all")
		(items ?resultItems)
	)
	
	=>
	
	(send-agree ?message "")
	
	(bind ?receivers (prepare-receivers ?message))
	
	; For refOp all nothing has to be checked. All results are just send to the receivers.
	(bind ?resultContent 
		(str-cat
			"((= "
			(strip-braces (fact-slot-value ?message "content"))
			" "
			(clips2sl ?resultItems)
			"))"
		)
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


(defrule fipa-query-queryRef-any
	"Fires for agent-message-evaluation-result facts with protocol query and performative query-ref."
	?message <- (agent-message
		(protocol "fipa-query")
		(performative "query-ref")
	)
	(agent-queryRef-result
		(message ?message)
		(refOp "any")
		(items ?resultItems)
	)
	
	=>
	
	(send-agree ?message "")
	
	(bind ?receivers (prepare-receivers ?message))
	
	; For refOp any we just return the first result.
	(bind ?resultContent 
		(str-cat
			"((= "
			(strip-braces (fact-slot-value ?message "content"))
			" "
			(clips2sl (first$ ?resultItems))
			"))"
		)
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


(defrule fipa-query-queryRef-iota
	"Fires for agent-message-evaluation-result facts with protocol query and performative query-ref."
	?message <- (agent-message
		(protocol "fipa-query")
		(performative "query-ref")
	)
	(agent-queryRef-result
		(message ?message)
		(refOp "iota")
		(items ?resultItems)
	)
	
	=>
	
	(send-agree ?message "")
	
	(bind ?receivers (prepare-receivers ?message))
	
	; For refOp iota we have to check if we have exactly one result
	(if
		(eq (length$ ?resultItems) 1)
	 then
		(bind ?resultContent 
			(str-cat
				"((= "
				(strip-braces (fact-slot-value ?message "content"))
				" "
				(clips2sl (first$ ?resultItems))
				"))"
			)
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
	 else
	 	(bind ?errorMessage "")
	 	(if
	 		(> (length$ ?resultItems) 1)
	 	 then
	 	 	(bind ?errorMessage "too-many-results")
	 	 else
	 	 	(bind ?errorMessage "no-results")
	 	)
	 	(bind ?resultContent 
			(str-cat
				"((= "
				(strip-braces (fact-slot-value ?message "content"))
				" "
				(clips2sl ?errorMessage)
				"))"
			)
		)
		
		(assert 
			(agent-message
				(receivers ?receivers)
				(performative "failure")
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
)