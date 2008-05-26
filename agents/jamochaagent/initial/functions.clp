; ===================================================
; Definition of functions that are needed.
; ===================================================

(deffunction process-incoming-message
	"Processes incoming messages with any protocol and performative."
	(functiongroup AgentFunctions)
	(?message)
	
	; Translate the Code according to the given performative.
	(bind ?clipsCode
	    (sl2clips
	    	(fact-slot-value ?message "performative")
	    	(fact-slot-value ?message "content")
	    )
	)
	
	; Set the error to an empty value.
	(bind ?error "")
	
	; We substitute the String %MSG% in the clips code with the actual message-fact
	; to give the performatives the possibility to use a reference in their results.
	(bind ?clipsCode
		(str-replace-all
			?clipsCode
			"%MSG%"
			(str-cat "(fact-id " (get-fact-id ?message) ")")
		)
	)
	
	; Evaluate the code. The performatives are responsible for creating their specific
	; result-facts.
	(eval ?clipsCode ?error)
	
	; Here we have to check for Errors that occured during evaluation.
	
	(if
		(neq ?error NIL)
	 then
	 	(if
			(> (str-length ?error) 0)
		 then
		 	(assert
		 		(agent-evaluation-error
		 			(message ?message)
		 			(error ?error)
		 		)
		 	)
		)
	)
	
	
	; Add the translated code to the initial message and set the message to processed.
	(modify ?message (content-clips ?clipsCode) (processed TRUE))
)


(deffunction process-outgoing-message
	"Processes (= sends) outgoing messages."
	(functiongroup AgentFunctions)
	(?message ?sender)
	
	(if (eq NIL (fact-slot-value ?message "sender")) then
		(modify ?message (sender ?sender) (timestamp (ms-time)) (processed TRUE))
	else
		(modify ?message (timestamp (ms-time)) (processed TRUE))
	)
	(agent-send-message ?message)
)


(deffunction strip-braces
	"Removes a brace from the left and the right end of a String."
	(functiongroup AgentFunctions)
	(?aString)
	(sub-string 1 (- (str-length ?aString) 1) ?aString)
)


(deffunction prepare-receivers
	"Puts together the receivers of a message by extracting the old sender and the reply-to content."
	(functiongroup AgentFunctions)
	(?message)
	(bind ?receivers
		(union$
			(create$ (fact-slot-value ?message "sender"))
			(fact-slot-value ?message "reply-to")
		)
	)
	; delete NIL values in the list.
	(return (delete-member$ ?receivers NIL))
)

(deffunction send-agree
	"Sends an agree to a message using the same protocol with optional propositions."
	(functiongroup AgentFunctions)
	(?message ?action ?proposition)
	
	(bind ?receivers (prepare-receivers ?message))
	(bind ?newContent ?proposition)
	(if (neq ?action "") then (bind ?newContent (str-cat "(" ?action ?newContent ")")))
	
	(assert 
		(agent-message
			(receiver ?receivers)
			(performative "agree")
			(content ?newContent)
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
	
	(fire)
)


(deffunction create-reply
	"Creates a simple reply by using all fields in the original message that are suitable."
	(functiongroup AgentFunctions)
	(?message)
	
	(bind ?receivers (prepare-receivers ?message))
	(assert 
		(agent-message
			(receiver ?receivers)
			(performative "")
			(content "")
			(language (fact-slot-value ?message "language"))
			(encoding (fact-slot-value ?message "encoding"))
			(ontology (fact-slot-value ?message "ontology"))
			(protocol (fact-slot-value ?message "protocol"))
			(conversation-id (fact-slot-value ?message "conversation-id"))
			(in-reply-to (fact-slot-value ?message "reply-with"))
			(incoming FALSE)
			(is-template TRUE)
		)
	)
)


(deffunction agent-identifiers-to-names
	"Converts a list of agent-indentifier facts to a list of their names."
	(functiongroup AgentFunctions)
	(?receivers)
	
	(bind ?receiverList (create$ ))
	(bind ?receiversTmp ?receivers)
	(while (> (length$ ?receiversTmp) 0) do
		(bind ?receiverList
			(insert$ ?receiverList 1 
				(fact-slot-value
					(first$ ?receiversTmp)
					"name"
				)
			)
		)
		(bind ?receiversTmp (rest$ ?receiversTmp))
	)
	(return ?receiverList)
)