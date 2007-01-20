(deftemplate agent-description
        (slot name)
)

(deftemplate agent-message
        (slot sender)
        (multislot receivers)
        (multislot reply-to)
        (slot performative)
        (slot content)
        (slot language)
        (slot encoding)
        (slot ontology)
        (slot protocol)
        (slot conversation-id)
        (slot in-reply-to)
        (slot reply-with)
        (slot reply-by)
        (multislot user-properties)
)

(assert
        (agent-description
                (name "Receiver")
        )
)
               
(assert
        (agent-message
                (sender "Another agent")
                (receivers "Receiver" "rec")
                (reply-to "reply-to")
                (performative "performative")
                (content "content")
                (language "language")
                (encoding "encoding")
                (ontology "ontology")
                (protocol "protocol")
                (conversation-id "conversation-id")
                (in-reply-to "in-reply-to")
                (reply-with "reply-with")
                (reply-by "reply-by")
                (user-properties "user-properties")
        )
)

(defrule incoming-message
        (agent-description
                (name ?name)
        )
        (agent-message
                (receivers ?name)
        )
=>
        (printout t ?name " received a message" crlf)
)
