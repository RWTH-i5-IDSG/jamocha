; ===================================================
; Definition of rules that handle the protocol
; fipa-query for all possible performatives.
; ===================================================

(defrule fipa-query-queryIf
	"Fires for agent-message-evaluation-result facts with protocol query and performative query-if."
	?initiator <- (agent-message-evaluation-result
		(receiver ?receiver)
		(refering-message ?oldMessage)
		(protocol "fipa-query")
		(previous-performative "query-if")
		(processed FALSE)
		(result ?result)
		(error ?error)
	)
	
	=>
	
	(bind ?receivers (prepare-receivers ?oldMessage))
	(if
		(eq ?error "")
	; no error occured
	then
		(bind ?resultContent (fact-slot-value ?oldMessage "content"))
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
		
		(assert (agent-message
			(receivers ?receivers)
			(performative "inform")
			(content ?resultContent)
			(language (fact-slot-value ?oldMessage "language"))
			(encoding (fact-slot-value ?oldMessage "encoding"))
			(ontology (fact-slot-value ?oldMessage "ontology"))
			(protocol (fact-slot-value ?oldMessage "protocol"))
			(conversation-id (fact-slot-value ?oldMessage "conversation-id"))
			(in-reply-to (fact-slot-value ?oldMessage "reply-with"))
			(reply-with "")
			(reply-by 0)
			(timestamp (datetime2timestamp (now)))
			(incoming FALSE)
		))
	; an error occured
	else
		(assert (agent-message
			(receivers ?receivers)
			(performative "refuse")
			(content (str-cat (fact-slot-value ?oldMessage "content") crlf "\"" ?error "\""))
			(language (fact-slot-value ?oldMessage "language"))
			(encoding (fact-slot-value ?oldMessage "encoding"))
			(ontology (fact-slot-value ?oldMessage "ontology"))
			(protocol (fact-slot-value ?oldMessage "protocol"))
			(conversation-id (fact-slot-value ?oldMessage "conversation-id"))
			(in-reply-to (fact-slot-value ?oldMessage "reply-with"))
			(reply-with "")
			(reply-by 0)
			(timestamp (datetime2timestamp (now)))
			(incoming FALSE)
		))
	)
	
	(modify ?initiator (processed TRUE))
)


(defrule fipa-query-queryRef
	"Fires for agent-message-evaluation-result facts with protocol query and performative query-ref."
	?initiator <- (agent-message-evaluation-result
		(receiver ?receiver)
		(refering-message ?oldMessage)
		(protocol "fipa-query")
		(previous-performative "query-ref")
		(result ?result)
		(processed FALSE)
		(error ?error)
	)
	
	=>
	
	(bind ?receivers (prepare-receivers ?oldMessage))
	
	(if
		(eq ?error "")
	; no error occured
	then
		(assert (agent-message
			(receivers ?receivers)
			(performative "agree")
			(content (fact-slot-value ?oldMessage "content"))
			(language (fact-slot-value ?oldMessage "language"))
			(encoding (fact-slot-value ?oldMessage "encoding"))
			(ontology (fact-slot-value ?oldMessage "ontology"))
			(protocol (fact-slot-value ?oldMessage "protocol"))
			(conversation-id (fact-slot-value ?oldMessage "conversation-id"))
			(in-reply-to (fact-slot-value ?oldMessage "reply-with"))
			(reply-with "")
			(reply-by 0)
			(timestamp (datetime2timestamp (now)))
			(incoming FALSE)
		))
		
		; This fire is needed to immediately send the agree
		(fire)
	
		(bind ?refOp (fact-slot-value ?result "refOp")
		
		(apply (str-cat "fipa-queryRef-handler-" ?refOp) ?receivers ?oldMessage ?result)
	; an error occured
	else
		(assert (agent-message
			(receivers ?receivers)
			(performative "refuse")
			(content (str-cat (fact-slot-value ?oldMessage "content") crlf "\"" ?error "\""))
			(language (fact-slot-value ?oldMessage "language"))
			(encoding (fact-slot-value ?oldMessage "encoding"))
			(ontology (fact-slot-value ?oldMessage "ontology"))
			(protocol (fact-slot-value ?oldMessage "protocol"))
			(conversation-id (fact-slot-value ?oldMessage "conversation-id"))
			(in-reply-to (fact-slot-value ?oldMessage "reply-with"))
			(reply-with "")
			(reply-by 0)
			(timestamp (datetime2timestamp (now)))
			(incoming FALSE)
		))
	)
	(modify ?initiator (processed TRUE))
)


; ===================================================
; Definition of functions that handle the different
; referential operators for query-ref
; ===================================================

(deffunction fipa-queryRef-handler-all
	"Handles query-ref performatives with referential operator all."
	(functiongroup AgentFunctions)
	(?receivers ?oldMessage ?resultFact)
	
	; For refOp all nothing has to be checked. All results are just send to the receivers.
	(bind ?resultContent 
		(str-cat "((= "
			(strip-braces (fact-slot-value ?oldMessage "content"))
		)
	)
		
	(bind ?resultSet (fact-slot-value ?resultFact "items"))
	
	(bind ?noConnected (fact-slot-value ?resultFact "noConnected"))
	
	(bind ?itemCount (length$ ?resultSet))
	(bind ?counter 1)
	
	(bind ?resultContent (str-cat ?resultContent "))"))
	
	(assert (agent-message
		(receivers ?receivers)
		(performative "inform")
		(content ?resultContent)
		(language (fact-slot-value ?oldMessage "language"))
		(encoding (fact-slot-value ?oldMessage "encoding"))
		(ontology (fact-slot-value ?oldMessage "ontology"))
		(protocol (fact-slot-value ?oldMessage "protocol"))
		(conversation-id (fact-slot-value ?oldMessage "conversation-id"))
		(in-reply-to (fact-slot-value ?oldMessage "reply-with"))
		(reply-with "")
		(reply-by 0)
		(timestamp (datetime2timestamp (now)))
		(incoming FALSE)
	))
)


(deffunction fipa-queryRef-handler-any
	"Handles query-ref performatives with referential operator any."
	(functiongroup AgentFunctions)
	(?receivers ?oldMessage ?resultSet)
	
	; For refOp any we just return the first result.
	(bind ?resultContent
		(str-cat 
			"((= "
			(strip-braces (fact-slot-value ?oldMessage "content"))
			(clips2sl (first$ ?resultSet)) "))"
		)
	)
	
	(assert (agent-message
		(receivers ?receivers)
		(performative "inform")
		(content ?resultContent)
		(language (fact-slot-value ?oldMessage "language"))
		(encoding (fact-slot-value ?oldMessage "encoding"))
		(ontology (fact-slot-value ?oldMessage "ontology"))
		(protocol (fact-slot-value ?oldMessage "protocol"))
		(conversation-id (fact-slot-value ?oldMessage "conversation-id"))
		(in-reply-to (fact-slot-value ?oldMessage "reply-with"))
		(reply-with "")
		(reply-by 0)
		(timestamp (datetime2timestamp (now)))
		(incoming FALSE)
	))
)