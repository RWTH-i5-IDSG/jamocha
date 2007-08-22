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
	(bind ?content
		(clips2sl (str-cat "(resolvePartyAnnouncement " ?announcementFact ")") )
	)
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