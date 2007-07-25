(deftemplate ingredient
	(slot name)
	(slot isMeat (default false) )
	(slot isVegetable (default false) )
	(slot isSpice (default false) )
	(slot isHerbs (default false) )
	(slot isDressing (default false) )
	(slot calories)
)

(deftemplate wurst
	(slot name)
	(slot color)
	(slot weight)
	(multislot ingredients)
	(slot price)
)

(deftemplate salat
	(slot name)
	(slot color)
	(slot weight)
	(multislot ingredients)
	(slot price)
)

(deftemplate drink
	(slot name)
	(slot color)
	(slot price)
	(multislot ingredients)
)
