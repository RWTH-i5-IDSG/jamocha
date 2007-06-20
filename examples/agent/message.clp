(deftemplate agent-message
       (slot sender)
       (slot receivers)
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

(deftemplate agent-description
       (slot name)
)

(defrule incoming-message
       (agent-description
               (name ?receiver)
       )
       (agent-message
               (sender ?sender)
               (receivers ?receiver)
               (reply-to ?replyto)
               (performative ?performative)
               (content ?content)
               (language ?language)
               (encoding ?encoding)
               (ontology ?ontology)
               (protocol ?protocol)
               (conversation-id ?conversationid)
               (in-reply-to ?inreplyto)
               (reply-with ?replywith)
               (reply-by ?replyby)
               (user-properties ?userproperties)
       )
       =>
       (printout t ?receiver " received a message" crlf)
       (agent-send-message ?sender ?replyto ?performative ?content ?language ?encoding ?ontology ?protocol ?conversationid)
)
(assert (agent-description (name "you") ) )
(assert (agent-message (sender "me")(receivers "you")(reply-to "peter" "paul" "marry")(performative "yes")(content "hello")(langauge "en")(encoding "utf8")(ontology "none")(protocol "1")(conversationid "100") ) )

