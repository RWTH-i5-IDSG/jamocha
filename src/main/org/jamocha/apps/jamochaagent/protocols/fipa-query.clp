; ===================================================
; Definition of rules that handle the protocol
; fipa-query for all possible performatives.
; ===================================================

(defrule fipa-query-queryIf
	"Fires for agent-message-initiators with protocol query and performative query-if."
	?initiator <- (agent-message-initiator
		(receiver ?receiver)
		(refering-message ?oldMessage)
		(protocol "fipa-query")
		(previous-performative "query-if")
		(processed FALSE)
		(error ?error)
	)
	
	=>
	
	(bind ?receivers
		(union$
			(create$ (fact-slot-value ?receiver "name"))
			(fact-slot-value ?oldMessage "reply-to")
		)
	)
	
	(bind ?receivers (delete-member$ ?receivers NIL))
	
	(bind ?result (fact-slot-value ?oldMessage "content"))
	(if
		(eq ?*query-if-result* FALSE)
	then
		(bind ?result
			(str-cat 
				"((not "
				(sub-string 1 (- (str-length ?result) 1) ?result)
				"))"
			)
		)
	)
	
	(assert (agent-message
		(receivers ?receivers)
		(performative "inform")
		(content ?result)
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
	(fire)
	
	(modify ?initiator (processed TRUE))
)



(defrule fipa-query-queryRef
	"Fires for agent-message-initiators with protocol query and performative query-ref."
	?initiator <- (agent-message-initiator
		(receiver ?receiver)
		(refering-message ?oldMessage)
		(protocol "fipa-query")
		(previous-performative "query-ref")
		(processed FALSE)
		(error ?error)
	)
	
	=>
	
	(bind ?receivers
		(union$
			(create$ (fact-slot-value ?receiver "name"))
			(fact-slot-value ?oldMessage "reply-to")
		)
	)
	
	(bind ?receivers (delete-member$ ?receivers NIL))
	
	(bind ?result (fact-slot-value ?oldMessage "content"))
	(bind ?result
		(str-cat 
			"((= "
			(sub-string 1 (- (str-length ?result) 1) ?result)
			(clips2sl ?*query-ref-result*) "))"
		)
	)
	
	(assert (agent-message
		(receivers ?receivers)
		(performative "inform")
		(content ?result)
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
	(fire)
	
	(modify ?initiator (processed TRUE))
)