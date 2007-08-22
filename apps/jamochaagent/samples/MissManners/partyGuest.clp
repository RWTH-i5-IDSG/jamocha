(batch apps/jamochaagent/samples/MissManners/common.clp)


; (register "PartyHost@amufusims-mac.local:1099/JADE" "heinz" "m" (create$ "wurst" "bier" "senf"))
; (register "PartyHost@amufusims-mac.local:1099/JADE" "lissbett" "w" (create$  "bier" "senf"))
; (register "PartyHost@amufusims-mac.local:1099/JADE" "will" "m" (create$ "wurst" ))

(deffunction register
;params:
	(?partyHost ?name ?sex ?hobbies)
	
	(bind ?announcementFact
		(assert
			(PartyAnnouncement
				(name ?name)
				(sex ?sex)
				(hobbies ?hobbies)
			)
		)
	)
	
	(bind ?content (str-cat
		"((action (agent-identifier :name "
		(clips2sl ?partyHost)
		")"
		"(resolvePartyAnnouncement "
		(clips2sl ?announcementFact)
		")))"
   ))
	(assert 
		(agent-message
			(receiver ?partyHost)
			(performative "request")
			(protocol "fipa-request")
			(content ?content)
			(language "fipa-sl")
			(encoding "")
			(ontology "MissManners")
			(conversation-id (str-cat (agent-name) (ms-time)))
			(in-reply-to "")
			(reply-with "123")
			(reply-by 0)
			(timestamp (datetime2timestamp (now)))
			(incoming FALSE)
		)
	)
	(fire)
)
