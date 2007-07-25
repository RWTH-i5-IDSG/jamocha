; IMPORTANT: working rules should be without a comment BUT
; not working rules should be commented out in order to signal
; a ToDo. Working rules should be appended by the _correct_ output
; which have to be commented out by double ";;" for an automatic
; evaluation and comparison

;(defrule wurst-ohne-fleisch
;	(wurst (name ?sausage_name) (ingredients $?sausage_ingredients ) )
;	(not
;		(ingredient (name ?name) (isMeat true) )
;		(test (member$ ?name $?sausage_ingredients) )
;	)
;	=>	
;	(printout t "[wurst-ohne-fleisch] a sausage without meat is " ?sausage_name "." crlf )
;)

(defrule vegetable-meat-dressing-menu-with-less-than-250-cals
	(ingredient (name ?meatName) (isMeat true) (calories ?meatCal))
	(ingredient (name ?dressingName) (isDressing true) (calories ?dressingCal) )
	(ingredient (name ?vegetableName) (isVegetable true) (calories ?vegetableCal) )
	(test (less (add ?meatCal ?dressingCal ?vegetableCal) 250) )
	=>
	(printout t "[vegetable-meat-dressing-menu-with-less-than-250-cals] a vegetable,meat,dressing combination with at less than 250 calories is " ?meatName "," ?vegetableName "," ?dressingName "." crlf)
)
;;[vegetable-meat-dressing-menu-with-less-than-250-cals] a vegetable,meat,dressing combination with at less than 250 calories is chicken,lead salad,ketchup.
;;[vegetable-meat-dressing-menu-with-less-than-250-cals] a vegetable,meat,dressing combination with at less than 250 calories is chicken,lead salad,yoghurt.
;;[vegetable-meat-dressing-menu-with-less-than-250-cals] a vegetable,meat,dressing combination with at less than 250 calories is chicken,lead salad,mustard.
;;[vegetable-meat-dressing-menu-with-less-than-250-cals] a vegetable,meat,dressing combination with at less than 250 calories is chicken,tomato,ketchup.
;;[vegetable-meat-dressing-menu-with-less-than-250-cals] a vegetable,meat,dressing combination with at less than 250 calories is beef,lead salad,yoghurt.
;;[vegetable-meat-dressing-menu-with-less-than-250-cals] a vegetable,meat,dressing combination with at less than 250 calories is chicken,tomato,yoghurt.
;;[vegetable-meat-dressing-menu-with-less-than-250-cals] a vegetable,meat,dressing combination with at less than 250 calories is chicken,tomato,mustard.
;;[vegetable-meat-dressing-menu-with-less-than-250-cals] a vegetable,meat,dressing combination with at less than 250 calories is chicken,gherkin,yoghurt.
;;[vegetable-meat-dressing-menu-with-less-than-250-cals] a vegetable,meat,dressing combination with at less than 250 calories is chicken,gherkin,mustard.
;;[vegetable-meat-dressing-menu-with-less-than-250-cals] a vegetable,meat,dressing combination with at less than 250 calories is chicken,garlic,yoghurt.
;;[vegetable-meat-dressing-menu-with-less-than-250-cals] a vegetable,meat,dressing combination with at less than 250 calories is beef,tomato,yoghurt.
;;[vegetable-meat-dressing-menu-with-less-than-250-cals] a vegetable,meat,dressing combination with at less than 250 calories is chicken,onion,yoghurt.
;;[vegetable-meat-dressing-menu-with-less-than-250-cals] a vegetable,meat,dressing combination with at less than 250 calories is chicken,onion,mustard.
