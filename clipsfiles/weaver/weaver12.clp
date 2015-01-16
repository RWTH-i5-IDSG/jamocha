

(defrule p414
  (context (present find-no-of-vcg-hcg))
  (net (net-name ?nn) (no-of-bottom-pins ?np&:(> ?np 0)) (net-is-routed ~yes))
  (not (total-verti ?nn bottom ? ?))
  (ff (net-name ?nn) (grid-x ?garb1) (grid-y ?garb2) (grid-layer ?garb3) (pin-name ?pn))
  (pin (net-name ?nn) (pin-name ?pn) (pin-channel-side bottom))
  (constraint (constraint-type horizontal | vertical) (net-name-1 ?nn))
  =>
  (assert (total-verti ?nn bottom 0 ?np))
)

(defrule p415
  (context (present find-no-of-vcg-hcg))
  (net (net-name ?nn) (no-of-bottom-pins ?np&:(> ?np 0)) (net-is-routed ~yes))
  (not (total-verti ?nn bottom ? ?))
  (ff (net-name ?nn) (grid-x ?garb1) (grid-y ?garb2) (grid-layer ?garb3) (pin-name ?pn))
  (pin (net-name ?nn) (pin-name ?pn) (pin-channel-side bottom))
  (constraint (constraint-type horizontal | vertical) (net-name-2 ?nn))
  =>
  (assert (total-verti ?nn bottom 0 ?np))
)

(defrule p416
  (context (present find-no-of-vcg-hcg))
  (ff (net-name ?nn) (grid-x ?gx) (grid-y ?garb2) (grid-layer ?garb3) (pin-name ?garb1))
  (pin (net-name ?nn) (pin-x ?gx) (pin-channel-side top) (pin-name ?pn1))
  (net (net-name ?nn1&~?nn) (no-of-bottom-pins ?np&:(> ?np 0)) (net-is-routed ~yes))
  (pin (net-name ?nn1) (pin-x ?gx) (pin-channel-side bottom) (pin-name ?pn2))
  (not (total-verti ?nn1 bottom ? ?))
  (not (constraint (constraint-type vertical) (net-name-1 ?nn) (net-name-2 ?nn1) (pin-name-1 ?pn1) (pin-name-2 ?pn2)))
  =>
  (assert (counted-verti ?nn1 bottom ?nn ?pn1 ?pn2))
  (assert (total-verti ?nn1 bottom 1 ?np))
)

(defrule p417
  (context (present find-no-of-vcg-hcg))
  (net (net-name ?nn) (no-of-left-pins ?np&:(> ?np 1)) (net-is-routed ~yes))
  (not (total-verti ?nn left ? ?))
  (ff (net-name ?nn) (grid-x ?garb1) (grid-y ?garb2) (grid-layer ?garb3) (pin-name ?pn))
  (pin (net-name ?nn) (pin-name ?pn) (pin-channel-side left))
  =>
  (assert (total-verti ?nn left 0 ?np))
)

(defrule p418
  (context (present find-no-of-vcg-hcg))
  (net (net-name ?nn) (no-of-left-pins ?np&:(> ?np 0)) (net-is-routed ~yes))
  (not (total-verti ?nn left ? ?))
  (ff (net-name ?nn) (grid-x ?garb1) (grid-y ?garb2) (grid-layer ?garb3) (pin-name ?pn))
  (pin (net-name ?nn) (pin-name ?pn) (pin-channel-side left))
  (constraint (constraint-type horizontal | vertical) (net-name-1 ?nn))
  =>
  (assert (total-verti ?nn left 0 ?np))
)



(defrule p419
  (context (present find-no-of-vcg-hcg))
  (net (net-name ?nn) (no-of-left-pins ?np&:(> ?np 0)) (net-is-routed ~yes))
  (not (total-verti ?nn left ? ?))
  (ff (net-name ?nn) (grid-x ?garb1) (grid-y ?garb2) (grid-layer ?garb3) (pin-name ?pn))
  (pin (net-name ?nn) (pin-name ?pn) (pin-channel-side left))
  (constraint (constraint-type horizontal | vertical) (net-name-2 ?nn))
  =>
  (assert (total-verti ?nn left 0 ?np))
)

(defrule p420
  (context (present find-no-of-vcg-hcg))
  (ff (net-name ?nn) (grid-x ?garb1) (grid-y ?gy) (grid-layer ?garb3) (pin-name ?garb2))
  (pin (net-name ?nn) (pin-y ?gy) (pin-channel-side right) (pin-name ?pn1))
  (net (net-name ?nn1&~?nn) (no-of-left-pins ?np&:(> ?np 0)) (net-is-routed ~yes))
  (pin (net-name ?nn1) (pin-y ?gy) (pin-channel-side left) (pin-name ?pn2))
  (not (total-verti ?nn1 left ? ?))
  (not (constraint (constraint-type horizontal) (net-name-1 ?nn) (net-name-2 ?nn1) (pin-name-1 ?pn1) (pin-name-2 ?pn2)))
  =>
  (assert (counted-verti ?nn1 left ?nn ?pn1 ?pn2))
  (assert (total-verti ?nn1 left 1 ?np))
)

(defrule p421
  (context (present find-no-of-vcg-hcg))
  (net (net-name ?nn) (no-of-top-pins ?np&:(> ?np 1)) (net-is-routed ~yes))
  (not (total-verti ?nn top ? ?))
  (ff (net-name ?nn) (grid-x ?garb1) (grid-y ?garb2) (grid-layer ?garb3) (pin-name ?pn))
  (pin (net-name ?nn) (pin-name ?pn) (pin-channel-side top))
  =>
  (assert (total-verti ?nn top 0 ?np))
)

(defrule p422
  (context (present find-no-of-vcg-hcg))
  (net (net-name ?nn) (no-of-top-pins ?np&:(> ?np 0)) (net-is-routed ~yes))
  (not (total-verti ?nn top ? ?))
  (ff (net-name ?nn) (grid-x ?garb1) (grid-y ?garb2) (grid-layer ?garb3) (pin-name ?pn))
  (pin (net-name ?nn) (pin-name ?pn) (pin-channel-side top))
  (constraint (constraint-type horizontal | vertical) (net-name-1 ?nn))
  =>
  (assert (total-verti ?nn top 0 ?np))
)

(defrule p423
  (context (present find-no-of-vcg-hcg))
  (net (net-name ?nn) (no-of-top-pins ?np&:(> ?np 0)) (net-is-routed ~yes))
  (not (total-verti ?nn top ? ?))
  (ff (net-name ?nn) (grid-x ?garb1) (grid-y ?garb2) (grid-layer ?garb3) (pin-name ?pn))
  (pin (net-name ?nn) (pin-name ?pn) (pin-channel-side top))
  (constraint (constraint-type horizontal | vertical) (net-name-2 ?nn))
  =>
  (assert (total-verti ?nn top 0 ?np))
)

(defrule p424
  (context (present find-no-of-vcg-hcg))
  (net (net-name ?nn) (no-of-right-pins ?np&:(> ?np 1)) (net-is-routed ~yes))
  (not (total-verti ?nn right ? ?))
  (ff (net-name ?nn) (grid-x ?garb1) (grid-y ?garb2) (grid-layer ?garb3) (pin-name ?pn))
  (pin (net-name ?nn) (pin-name ?pn) (pin-channel-side right))
  =>
  (assert (total-verti ?nn right 0 ?np))
)

(defrule p425
  (context (present find-no-of-vcg-hcg))
  (net (net-name ?nn) (no-of-right-pins ?np&:(> ?np 0)) (net-is-routed ~yes))
  (not (total-verti ?nn right ? ?))
  (ff (net-name ?nn) (grid-x ?garb1) (grid-y ?garb2) (grid-layer ?garb3) (pin-name ?pn))
  (pin (net-name ?nn) (pin-name ?pn) (pin-channel-side right))
  (constraint (constraint-type horizontal | vertical) (net-name-1 ?nn))
  =>
  (assert (total-verti ?nn right 0 ?np))
)

(defrule p426
  (context (present find-no-of-vcg-hcg))
  (net (net-name ?nn) (no-of-right-pins ?np&:(> ?np 0)) (net-is-routed ~yes))
  (not (total-verti ?nn right ? ?))
  (ff (net-name ?nn) (grid-x ?garb1) (grid-y ?garb2) (grid-layer ?garb3) (pin-name ?pn))
  (pin (net-name ?nn) (pin-name ?pn) (pin-channel-side right))
  (constraint (constraint-type horizontal | vertical) (net-name-2 ?nn))
  =>
  (assert (total-verti ?nn right 0 ?np))
)

(defrule p427
  (context (present find-no-of-vcg-hcg))
  ?f2 <- (total-verti ?nn bottom ?ntc ? ?tvt1)
  (constraint (constraint-type vertical) (net-name-1 ?tn) (net-name-2 ?nn) (pin-name-1 ?tpn) (pin-name-2 ?bpn))
  (not (counted-verti ?nn bottom ?tn ?tpn ?bpn))
  =>
  (assert (counted-verti ?nn bottom ?tn ?tpn ?bpn))
  (retract ?f2)
  (assert (total-verti ?nn bottom ?ntc =(+ ?ntc 1) ?tvt1))
)

(defrule p428
  (context (present find-no-of-vcg-hcg))
  ?f2 <- (total-verti ?nn left ?ntc ? ?tvt1)
  (constraint (constraint-type horizontal) (net-name-1 ?tn) (net-name-2 ?nn) 
              (pin-name-1 ?tpn) (pin-name-2 ?bpn))
  (not (counted-verti ?nn left ?tn ?tpn ?bpn))
  =>
  (assert (counted-verti ?nn left ?tn ?tpn ?bpn))
  (retract ?f2)
  (assert (total-verti ?nn left ?ntc =(+ ?ntc 1) ?tvt1))
)

(defrule p429
  (context (present find-no-of-vcg-hcg))
  ?f2 <- (total-verti ?nn top ?ntc ? ?tvt1)
  (constraint (constraint-type vertical) (net-name-1 ?nn) (net-name-2 ?bn) 
              (pin-name-1 ?tpn) (pin-name-2 ?bpn))
  (not (counted-verti ?nn top ?bn ?tpn ?bpn))
  =>
  (assert (counted-verti ?nn top ?bn ?tpn ?bpn))
  (retract ?f2)
  (assert (total-verti ?nn top ?ntc =(+ ?ntc 1) ?tvt1))
)

(defrule p430
  (context (present find-no-of-vcg-hcg))
  ?f2 <- (total-verti ?nn right ?ntc ? ?tvt1)
  (constraint (constraint-type horizontal) (net-name-1 ?nn) (net-name-2 ?bn) 
              (pin-name-1 ?tpn) (pin-name-2 ?bpn))
  (not (counted-verti ?nn right ?bn ?tpn ?bpn))
  =>
  (assert (counted-verti ?nn right ?bn ?tpn ?bpn))
  (retract ?f2)
  (assert (total-verti ?nn right ?ntc =(+ ?ntc 1) ?tvt1))
)

(defrule p431
  ?f1 <- (context (present find-no-of-vcg-hcg))
  =>
  (retract ?f1)
  (assert (context (previous find-no-of-vcg-hcg)))
)

(defrule p432
  ?f1 <- (context (previous find-no-of-vcg-hcg))
  (total-verti $?)
  =>
  (retract ?f1)
  (assert (context (present choose-between-total-verti)))
  (assert (context (present choose-between-total-verti-0)))
)

(defrule p433
  (context (present choose-between-total-verti))
  (not (total-verti ? ? 0 ?))
  ?t <- (total-verti ?nn bottom ?garb ?t1&:(> ?t1 1))
  (not (ff (net-name ?nn) (came-from south)))
  =>
  (retract ?t)
)

(defrule p434
  (context (present choose-between-total-verti))
  (not (total-verti ? ? 0 ?))
  ?t <- (total-verti ?nn top ?garb ?t1&:(> ?t1 1))
  (not (ff (net-name ?nn) (came-from north)))
  =>
  (retract ?t)
)

(defrule p435
  (context (present choose-between-total-verti))
  (not (total-verti ? ? 0 ?))
  ?t <- (total-verti ?nn left ?garb ?t1&:(> ?t1 1))
  (not (ff (net-name ?nn) (came-from west)))
  =>
  (retract ?t)
)

(defrule p436
  (context (present choose-between-total-verti))
  (not (total-verti ? ? 0 ?))
  ?t <- (total-verti ?nn right ?garb ?t1&:(> ?t1 1))
  (not (ff (net-name ?nn) (came-from east)))
  =>
  (retract ?t)
)

(defrule p437
  ?f1 <- (context (present choose-between-total-verti))
  =>
  (modify ?f1 (present extend-total-verti))
)

(defrule p438
  (context (present choose-between-total-verti))
  (total-verti ? ? ?qw86&:(> ?qw86 0) ?)
  ?f3 <- (total-verti ? ? 0 ?)
  =>
  (retract ?f3)
)

(defrule p439
  (context (present choose-between-total-verti))
  (total-verti ?nn ?tb&top|bottom ?t1&:(> ?t1 0))
  ?f3 <- (total-verti ?nn ~?tb&top|bottom 0)
  =>
  (retract ?f3)
)

(defrule p440
  (context (present choose-between-total-verti))
  (total-verti ?nn ?tb&left|right ?t1&:(> ?t1 0))
  ?f3 <- (total-verti ?nn ~?tb&left|right 0)
  =>
  (retract ?f3)
)

(defrule p441
  (context (present choose-between-total-verti-0))
  ?f2 <- (total-verti ?nn ?tb&top|bottom ?t1&:(> ?t1 0))
  ?f3 <- (total-verti ?nn ~?tb&top|bottom ?t2&:(> ?t2 0))
  =>
  (retract ?f2 ?f3)
)

(defrule p442
  (context (present choose-between-total-verti-0))
  ?f2 <- (total-verti ?nn ?tb&left|right ?t1&:(> ?t1 0))
  ?f3 <- (total-verti ?nn ~?tb&left|right ?t2&:(> ?t2 0))
  =>
  (retract ?f2 ?f3)
)

(defrule p443
  ?f1 <- (context (present choose-between-total-verti-0))
  =>
  (retract ?f1)
)

(defrule p444
  (context (present extend-total-verti))
  ?f2 <- (total-verti ?nn bottom ? ?qw86&:(> ?qw86 1))
  (not (total-verti ? ? ? 1))
  ?f3 <- (ff (came-from south) (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?pn))
  (not (ff (net-name ?nn) (grid-y ?qz20&:(< ?qz20 ?gy))))
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw87&:(> ?qw87 ?gy)) (com ?gx)))
  ?f4 <- (vertical (net-name nil) (min ?min&:(<= ?min ?gy)) (max ?qv2&:(and (< ?qv2 ?min) (< ?qv2 ?gy))) (com ?gx) (layer ?lay) (compo ?vcpo) (commo ?vcmo) (min-net ?mn))
  (congestion (direction row) (coordinate ?como) (como ?gy))
  (not (horizontal (net-name ~?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?como) (layer ?lay)))
  (not (vertical (net-name ~?nn&~nil) (min ?qw74&:(<= ?qw74 ?como)) (max ?qw62&:(>= ?qw62 ?como)) (com ?gx) (layer ?lay)))
  =>
  (assert (vertical (net-name nil) (min ?min) (max ?gy) (min-net ?mn) (max-net ?nn) (commo ?vcmo) (compo ?vcpo) (com ?gx) (layer ?lay)))
  (modify ?f4 (min ?como) (min-net ?nn))
  (assert (vertical (net-name ?nn) (min ?gy) (max ?como) (layer ?lay) (commo ?vcmo) (compo ?vcpo) (pin-name ?pn) (com ?gx)))
  (modify ?f3 (grid-y ?como) (can-chng-layer nil))
  (retract ?f2)
)

(defrule p445
  (context (present extend-total-verti))
  ?f2 <- (total-verti ?nn left ? ?qw87&:(> ?qw87 1))
  (not (total-verti ? ?  ? 1))
  ?f3 <- (ff (came-from west) (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?pn))
  (not (ff (net-name ?nn) (grid-x ?qz20&:(< ?qz20 ?gx))))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw88&:(> ?qw88 ?gx)) (com ?gy)))
  ?f4 <- (horizontal (net-name nil) (min ?min&:(<= ?min ?gx)) (max ?qv2&:(and (< ?qv2 ?min) (< ?qv2 ?gx))) (com ?gy) (layer ?lay) (compo ?hcpo) (commo ?hcmo) (min-net ?mn))
  (congestion (direction col) (coordinate ?como) (como ?gx))
  (not (horizontal (net-name ~?nn&~nil) (min ?qw74&:(<= ?qw74 ?como)) (max ?qw62&:(>= ?qw62 ?como)) (com ?gy) (layer ?lay)))
  (not (vertical (net-name ~?nn&~nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw63&:(>= ?qw63 ?gy)) (com ?como) (layer ?lay)))
  =>
  (assert (horizontal (net-name nil) (min ?min) (max ?gx) (min-net ?mn) (max-net ?nn) (commo ?hcmo) (compo ?hcpo) (com ?gy) (layer ?lay)))
  (modify ?f4 (min ?como) (min-net ?nn))
  (assert (horizontal (net-name ?nn) (min ?gx) (max ?como) (layer ?lay) (commo ?hcmo) (compo ?hcpo) (pin-name ?pn) (com ?gy)))
  (modify ?f3 (grid-x ?como) (can-chng-layer nil))
  (retract ?f2)
)



(defrule p446
  (context (present extend-total-verti))
  ?f2 <- (total-verti ?nn top ? ?qw87&:(> ?qw87 1))
  (not (total-verti ? ? ? 1))
  ?f3 <- (ff (came-from north) (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?pn))
  (not (ff (net-name ?nn) (grid-y ?qw86&:(> ?qw86 ?gy))))
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qz20&:(< ?qz20 ?gy)) (max ?qw63&:(>= ?qw63 ?gy)) (com ?gx)))
  ?f4 <- (vertical (net-name nil) (max ?max&:(>= ?max ?gy)) (min ?qz36&:(and (< ?qz36 ?max) (< ?qz36 ?gy))) (com ?gx) (layer ?lay) (compo ?vcpo) (commo ?vcmo) (max-net ?mn))
  (congestion (direction row) (coordinate ?gy) (como ?como))
  (not (horizontal (net-name ~?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?como) (layer ?lay)))
  (not (vertical (net-name ~?nn&~nil) (min ?qw74&:(<= ?qw74 ?como)) (max ?qw64&:(>= ?qw64 ?como)) (com ?gx) (layer ?lay)))
  =>
  (assert (vertical (net-name nil) (min ?gy) (max ?max) (max-net ?mn) (min-net ?nn) (commo ?vcmo) (compo ?vcpo) (com ?gx) (layer ?lay)))
  (modify ?f4 (max ?como) (max-net ?nn))
  (assert (vertical (net-name ?nn) (max ?gy) (min ?como) (layer ?lay) (commo ?vcmo) (compo ?vcpo) (pin-name ?pn) (com ?gx)))
  (modify ?f3 (grid-y ?como) (can-chng-layer nil))
  (retract ?f2)
)

(defrule p447
  (context (present extend-total-verti))
  ?f2 <- (total-verti ?nn right ? ?qw86&:(> ?qw86 1))
  (not (total-verti ? ? ? 1))
  ?f3 <- (ff (came-from east) (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?pn))
  (not (ff (net-name ?nn) (grid-x ?qw87&:(> ?qw87 ?gx))))
  (not (horizontal (net-name ?nn&~nil) (min ?qz20&:(< ?qz20 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (status nil) (com ?gy)))
  ?f4 <- (horizontal (net-name nil) (max ?max&:(>= ?max ?gx)) (min ?qz36&:(and (< ?qz36 ?max) (< ?qz36 ?gx))) (com ?gy) (layer ?lay) (compo ?hcpo) (commo ?hcmo) (max-net ?mn))
  (congestion (direction col) (coordinate ?gx) (como ?como))
  (not (horizontal (net-name ~?nn&~nil) (min ?qw74&:(<= ?qw74 ?como)) (max ?qw62&:(>= ?qw62 ?como)) (com ?gy) (layer ?lay)))
  (not (vertical (net-name ~?nn&~nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw63&:(>= ?qw63 ?gy)) (com ?como) (layer ?lay)))
  =>
  (assert (horizontal (net-name nil) (min ?gx) (max ?max) (max-net ?mn) (min-net ?nn) (commo ?hcmo) (compo ?hcpo) (com ?gy) (layer ?lay)))
  (modify ?f4 (max ?como) (max-net ?nn))
  (assert (horizontal (net-name ?nn) (max ?gx) (min ?como) (layer ?lay) (commo ?hcmo) (compo ?hcpo) (pin-name ?pn) (com ?gy)))
  (modify ?f3 (grid-x ?como) (can-chng-layer nil))
  (retract ?f2)
)

(defrule p448
  ?f1 <- (context (present extend-total-verti))
  ?c2 <- (total-verti ?nn bottom ? 1)
  (ff (came-from south) (net-name ?nn) (grid-x ?gx) (grid-y ?gy))
  ?c4 <- (vertical (status nil) (net-name ?nn&~nil) (min ?vmin&:(> ?vmin ?gy)) (com ?vcom&:(< ?vcom ?gx)) (layer ?lay) (pin-name ?pn))
  (not (vertical-s (net-name ~?nn) (com ?vcom)))
  (not (ff (net-name ?nn) (grid-x ?vcom) (grid-y ?vmin)))
  ?c6 <- (vertical (net-name nil) (min ?vvmin) (max ?vmin&:(> ?vmin ?vvmin)) (com ?vcom) (layer ?lay) (compo ?garb1) (commo ?garb2))
  (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?vcom)) (max ?qz65&:(and (> ?qz65 ?hmin) (>= ?qz65 ?gx))) (com ?hcom&:(and (>= ?hcom ?vvmin) (< ?hcom ?vmin))))
  (not (horizontal (net-name nil) (min ?qw74&:(<= ?qw74 ?vcom)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?qz49&:(and (>= ?qz49 ?vvmin) (< ?qz49 ?hcom)))))
  (not (vertical (status nil) (net-name ?nn&~nil) (com ?qz38&:(and (> ?qz38 ?vcom) (< ?qz38 ?gx)))))
  (not (horizontal (status nil) (net-name ?nn&~nil) (com ?qz61&:(and (< ?qz61 ?vmin) (> ?qz61 ?hcom)))))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw75&:(<= ?qw75 ?vcom)) (max ?qw62&:(>= ?qw62 ?vcom)) (com ?hcom) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw76&:(<= ?qw76 ?hcom)) (max ?qw63&:(>= ?qw63 ?hcom)) (com ?vcom) (layer ?lay)))
  =>
  (modify ?c6 (max ?hcom) (max-net ?nn))
  (modify ?c4 (min ?hcom))
  (assert (pull-ff north ?nn ?gx ?gy ?gx ?hcom))
  (assert (ff (net-name ?nn) (pin-name ?pn) (grid-layer ?lay) (grid-x ?vcom) (grid-y ?hcom) (came-from north)))
  (modify ?f1 (present find-no-of-pins-on-a-row-col))
)



(defrule p449
  ?f1 <- (context (present extend-total-verti))
  ?c2 <- (total-verti ?nn bottom ? 1)
  (ff (came-from south) (net-name ?nn) (grid-x ?gx) (grid-y ?gy))
  ?c4 <- (vertical (status nil) (net-name ?nn&~nil) (min ?vmin&:(> ?vmin ?gy)) (com ?vcom&:(< ?vcom ?gx)) (layer ?lay) (pin-name ?pn))
  (not (vertical-s (net-name ~?nn) (com ?vcom)))
  ?c5 <- (ff (net-name ?nn) (grid-x ?vcom) (grid-y ?vmin) (grid-layer ?garb1) (pin-name ?garb2))
  ?c7 <- (vertical (net-name nil) (min ?vvmin) (max ?vmin&:(> ?vmin ?vvmin)) (com ?vcom) (layer ?lay) (compo ?garb3) (commo ?garb4))
  (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?vcom)) (max ?qz65&:(and (> ?qz65 ?hmin) (>= ?qz65 ?gx))) (com ?hcom&:(and (>= ?hcom ?vvmin) (< ?hcom ?vmin))))
  (not (horizontal (net-name nil) (min ?qw74&:(<= ?qw74 ?vcom)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?qz49&:(and (>= ?qz49 ?vvmin) (< ?qz49 ?hcom)))))
  (not (vertical (status nil) (net-name ?nn&~nil) (com ?qz38&:(and (> ?qz38 ?vcom) (< ?qz38 ?gx)))))
  (not (horizontal (status nil) (net-name ?nn&~nil) (com ?qz61&:(and (< ?qz61 ?vmin) (> ?qz61 ?hcom)))))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw75&:(<= ?qw75 ?vcom)) (max ?qw62&:(>= ?qw62 ?vcom)) (com ?hcom) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw76&:(<= ?qw76 ?hcom)) (max ?qw63&:(>= ?qw63 ?hcom)) (com ?vcom) (layer ?lay)))
  =>
  (modify ?c7 (max ?hcom) (max-net ?nn))
  (modify ?c4 (min ?hcom))
  (assert (pull-ff north ?nn ?gx ?gy ?gx ?hcom))
  (modify ?c5 (grid-y ?hcom) (grid-layer ?lay))
  (modify ?f1 (present find-no-of-pins-on-a-row-col))
)

(defrule p450
  ?f1 <- (context (present extend-total-verti))
  ?c2 <- (total-verti ?nn bottom ? 1)
  (ff (came-from south) (net-name ?nn) (grid-x ?gx) (grid-y ?gy))
  ?c4 <- (vertical (status nil) (net-name ?nn&~nil) (min ?vmin&:(> ?vmin ?gy)) (com ?vcom&:(< ?vcom ?gx)) (layer ?lay) (pin-name ?pn))
  (not (vertical-s (net-name ~?nn) (com ?vcom)))
  ?c6 <- (vertical (net-name nil) (min ?vvmin) (max ?vmin&:(> ?vmin ?vvmin)) (com ?vcom) (layer ?lay) (compo ?garb1) (commo ?garb2))
  (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?vcom)) (max ?qz65&:(and (> ?qz65 ?hmin) (>= ?qz65 ?gx))) (com ?hcom&:(and (>= ?hcom ?vvmin) (< ?hcom ?vmin))))
  (not (horizontal (net-name nil) (min ?qw74&:(<= ?qw74 ?vcom)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?qz49&:(and (>= ?qz49 ?vvmin) (< ?qz49 ?hcom)))))
  (not (vertical (status nil) (net-name ?nn&~nil) (com ?qz38&:(and (> ?qz38 ?vcom) (< ?qz38 ?gx)))))
  (not (horizontal (status nil) (net-name ?nn&~nil) (com ?qz61&:(and (< ?qz61 ?vmin) (> ?qz61 ?hcom)))))
  ?c41 <- (vertical (status nil) (net-name ?nn&~nil) (min ?vmin1&:(> ?vmin1 ?gy)) (com ?vcom1&:(> ?vcom1 ?gx)) (layer ?lay) (pin-name ?pn1))
  (not (vertical-s (net-name ~?nn) (com ?vcom1)))
  ?c61 <- (vertical (net-name nil) (min ?vvmin1) (max ?vmin1&:(> ?vmin1 ?vvmin1)) 
                    (com ?vcom1) (layer ?lay))
  (horizontal (net-name nil) (min ?hmin1&:(<= ?hmin1 ?gx)) 
              (max ?qz66&:(and (> ?qz66 ?hmin1) (>= ?qz66 ?vcom1))) 
              (com ?hcom1&:(and (>= ?hcom1 ?vvmin1) (< ?hcom1 ?vmin1) (<= ?hcom1 ?hcom))))
  (not (horizontal (net-name nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw65&:(>= ?qw65 ?vcom1)) (com ?qz50&:(and (>= ?qz50 ?vvmin1) (< ?qz50 ?hcom1)))))
  (not (vertical (status nil) (net-name ?nn&~nil) (com ?qz39&:(and (> ?qz39 ?gx) (< ?qz39 ?vcom1)))))
  (not (horizontal (status nil) (net-name ?nn&~nil) (com ?qz62&:(and (< ?qz62 ?vmin1) (> ?qz62 ?hcom1)))))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw76&:(<= ?qw76 ?vcom1)) (max ?qw66&:(>= ?qw66 ?vcom1)) (com ?hcom1) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw77&:(<= ?qw77 ?hcom1)) (max ?qw64&:(>= ?qw64 ?hcom1)) (com ?vcom) (layer ?lay)))
  =>
  (modify ?c61 (max ?hcom1) (max-net ?nn))
  (modify ?c41 (min ?hcom1))
  (assert (pull-ff north ?nn ?gx ?gy ?gx ?hcom1))
  (assert (ff (net-name ?nn) (pin-name ?pn1) (grid-layer ?lay) (grid-x ?vcom1) (grid-y ?hcom1) (came-from north)))
  (modify ?f1 (present find-no-of-pins-on-a-row-col))
)

(defrule p451
  ?f1 <- (context (present extend-total-verti))
  ?c2 <- (total-verti ?nn bottom ? 1)
  (ff (came-from south) (net-name ?nn) (grid-x ?gx) (grid-y ?gy))
  ?c4 <- (vertical (status nil) (net-name ?nn&~nil) (min ?vmin&:(> ?vmin ?gy)) (com ?vcom&:(> ?vcom ?gx)) (layer ?lay) (pin-name ?pn))
  (not (vertical-s (net-name ~?nn) (com ?vcom)))
  (not (ff (net-name ?nn) (grid-x ?vcom) (grid-y ?vmin)))
  ?c6 <- (vertical (net-name nil) (min ?vvmin) (max ?vmin&:(> ?vmin ?vvmin)) (com ?vcom) (layer ?lay))
  (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?gx)) (max ?qz65&:(and (> ?qz65 ?hmin) (>= ?qz65 ?vcom))) (com ?hcom&:(and (>= ?hcom ?vvmin) (< ?hcom ?vmin))))
  (not (horizontal (net-name nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw62&:(>= ?qw62 ?vcom)) (com ?qz49&:(and (>= ?qz49 ?vvmin) (< ?qz49 ?hcom)))))
  (not (vertical (status nil) (net-name ?nn&~nil) (com ?qz38&:(and (> ?qz38 ?gx) (< ?qz38 ?vcom)))))
  (not (horizontal (status nil) (net-name ?nn&~nil) (com ?qz61&:(and (< ?qz61 ?vmin) (> ?qz61 ?hcom)))))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw74&:(<= ?qw74 ?vcom)) (max ?qw63&:(>= ?qw63 ?vcom)) (com ?hcom) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw75&:(<= ?qw75 ?hcom)) (max ?qw65&:(>= ?qw65 ?hcom)) (com ?vcom) (layer ?lay)))
  =>
  (modify ?c6 (max ?hcom) (max-net ?nn))
  (modify ?c4 (min ?hcom))
  (assert (pull-ff north ?nn ?gx ?gy ?gx ?hcom))
  (assert (ff (net-name ?nn) (pin-name ?pn) (grid-layer ?lay) (grid-x ?vcom) (grid-y ?hcom) (came-from north)))
  (modify ?f1 (present find-no-of-pins-on-a-row-col))
)

(defrule p452
  ?f1 <- (context (present extend-total-verti))
  ?c2 <- (total-verti ?nn bottom ? 1)
  (ff (came-from south) (net-name ?nn) (grid-x ?gx) (grid-y ?gy))
  ?c4 <- (vertical (status nil) (net-name ?nn&~nil) (min ?vmin&:(> ?vmin ?gy)) (com ?vcom&:(> ?vcom ?gx)) (layer ?lay) (pin-name ?pn))
  (not (vertical-s (net-name ~?nn) (com ?vcom)))
  ?c5 <- (ff (net-name ?nn) (grid-x ?vcom) (grid-y ?vmin) (grid-layer ?garb1) (pin-name ?garb2))
  ?c7 <- (vertical (net-name nil) (min ?vvmin) (max ?vmin&:(> ?vmin ?vvmin)) (com ?vcom) (layer ?lay))
  (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?gx)) (max ?qz65&:(and (> ?qz65 ?hmin) (>= ?qz65 ?vcom))) (com ?hcom&:(and (>= ?hcom ?vvmin) (< ?hcom ?vmin))))
  (not (horizontal (net-name nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw62&:(>= ?qw62 ?vcom)) (com ?qz49&:(and (>= ?qz49 ?vvmin) (< ?qz49 ?hcom)))))
  (not (vertical (status nil) (net-name ?nn&~nil) (com ?qz38&:(and (> ?qz38 ?gx) (< ?qz38 ?vcom)))))
  (not (horizontal (status nil) (net-name ?nn&~nil) (com ?qz61&:(and (< ?qz61 ?vmin) (> ?qz61 ?hcom)))))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw74&:(<= ?qw74 ?vcom)) (max ?qw63&:(>= ?qw63 ?vcom)) (com ?hcom) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw75&:(<= ?qw75 ?hcom)) (max ?qw64&:(>= ?qw64 ?hcom)) (com ?vcom) (layer ?lay)))
  =>
  (modify ?c7 (max ?hcom) (max-net ?nn))
  (modify ?c4 (min ?hcom))
  (assert (pull-ff north ?nn ?gx ?gy ?gx ?hcom))
  (modify ?c5 (grid-y ?hcom) (grid-layer ?lay))
  (modify ?f1 (present find-no-of-pins-on-a-row-col))
)

(defrule p453
  ?f1 <- (context (present extend-total-verti))
  ?c2 <- (total-verti ?nn top ? 1)
  (ff (came-from north) (net-name ?nn) (grid-x ?gx) (grid-y ?gy))
  ?c4 <- (vertical (status nil) (net-name ?nn&~nil) (min ?garb1) (max ?vmax&:(< ?vmax ?gy)) (com ?vcom&:(> ?vcom ?gx)) (layer ?lay) (compo ?garb2) (commo ?garb3) (pin-name ?pn))
  (not (vertical-s (net-name ~?nn) (com ?vcom)))
  (not (ff (net-name ?nn) (grid-x ?vcom) (grid-y ?vmax)))
  ?c6 <- (vertical (net-name nil) (min ?vmax) (max ?vvmax&:(> ?vvmax ?vmax)) (com ?vcom) (layer ?lay) (compo ?garb4) (commo ?garb5))
  (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?gx)) (max ?qz65&:(and (> ?qz65 ?hmin) (>= ?qz65 ?vcom))) (com ?hcom&:(and (<= ?hcom ?vvmax) (> ?hcom ?vmax))))
  (not (horizontal (net-name nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw62&:(>= ?qw62 ?vcom)) (com ?qz53&:(and (<= ?qz53 ?vvmax) (> ?qz53 ?hcom)))))
  (not (vertical (status nil) (net-name ?nn&~nil) (com ?qz38&:(and (> ?qz38 ?gx) (< ?qz38 ?vcom)))))
  (not (horizontal (status nil) (net-name ?nn&~nil) (com ?qz39&:(and (> ?qz39 ?vmax) (< ?qz39 ?hcom)))))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw74&:(<= ?qw74 ?vcom)) (max ?qw63&:(>= ?qw63 ?vcom)) (com ?hcom) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw75&:(<= ?qw75 ?hcom)) (max ?qw64&:(>= ?qw64 ?hcom)) (com ?vcom) (layer ?lay)))
  =>
  (modify ?c6 (min ?hcom) (min-net ?nn))
  (modify ?c4 (max ?hcom))
  (assert (pull-ff south ?nn ?gx ?gy ?gx ?hcom))
  (assert (ff (net-name ?nn) (pin-name ?pn) (grid-layer ?lay) (grid-x ?vcom) (grid-y ?hcom) (came-from south)))
  (modify ?f1 (present find-no-of-pins-on-a-row-col))
)

(defrule p454
  ?f1 <- (context (present extend-total-verti))
  ?c2 <- (total-verti ?nn top ? 1)
  (ff (came-from north) (net-name ?nn) (grid-x ?gx) (grid-y ?gy))
  ?c4 <- (vertical (status nil) (net-name ?nn&~nil) (min ?garb1) (max ?vmax&:(< ?vmax ?gy)) (com ?vcom&:(> ?vcom ?gx)) (layer ?lay) (pin-name ?pn))
  (not (vertical-s (net-name ~?nn) (com ?vcom)))
  ?c5 <- (ff (net-name ?nn) (grid-x ?vcom) (grid-y ?vmax) (grid-layer ?garb2) (pin-name ?garb3))
  ?c7 <- (vertical (net-name nil) (min ?vmax) (max ?vvmax&:(> ?vvmax ?vmax)) (com ?vcom) (layer ?lay) (compo ?garb4) (commo ?garb5))
  (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?gx)) (max ?qz65&:(and (> ?qz65 ?hmin) (>= ?qz65 ?vcom))) (com ?hcom&:(and (<= ?hcom ?vvmax) (> ?hcom ?vmax))))
  (not (horizontal (net-name nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw62&:(>= ?qw62 ?vcom)) (com ?qz53&:(and (<= ?qz53 ?vvmax) (> ?qz53 ?hcom)))))
  (not (vertical (status nil) (net-name ?nn&~nil) (com ?qz38&:(and (> ?qz38 ?gx) (< ?qz38 ?vcom)))))
  (not (horizontal (status nil) (net-name ?nn&~nil) (com ?qz39&:(and (> ?qz39 ?vmax) (< ?qz39 ?hcom)))))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw74&:(<= ?qw74 ?vcom)) (max ?qw63&:(>= ?qw63 ?vcom)) (com ?hcom) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw75&:(<= ?qw75 ?hcom)) (max ?qw64&:(>= ?qw64 ?hcom)) (com ?vcom) (layer ?lay)))
  =>
  (modify ?c7 (min ?hcom) (min-net ?nn))
  (modify ?c4 (max ?hcom))
  (assert (pull-ff south ?nn ?gx ?gy ?gx ?hcom))
  (modify ?c5 (grid-y ?hcom) (grid-layer ?lay))
  (modify ?f1 (present find-no-of-pins-on-a-row-col))
)

(defrule p455
  ?f1 <- (context (present extend-total-verti))
  ?c2 <- (total-verti ?nn top ? 1)
  (ff (came-from north) (net-name ?nn) (grid-x ?gx) (grid-y ?gy))
  ?c4 <- (vertical (status nil) (net-name ?nn&~nil) (min ?garb1) (max ?vmax&:(< ?vmax ?gy)) (com ?vcom&:(< ?vcom ?gx)) (layer ?lay) (compo ?garb2) (commo ?garb3) (pin-name ?pn))
  (not (vertical-s (net-name ~?nn) (com ?vcom)))
  (not (ff (net-name ?nn) (grid-x ?vcom) (grid-y ?vmax)))
  ?c6 <- (vertical (net-name nil) (min ?vmax) (max ?vvmax&:(> ?vvmax ?vmax)) (com ?vcom) (layer ?lay) (compo ?garb4) (commo ?garb5))
  (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?vcom)) (max ?qz65&:(and (> ?qz65 ?hmin) (>= ?qz65 ?gx))) (com ?hcom&:(and (<= ?hcom ?vvmax) (> ?hcom ?vmax))))
  (not (horizontal (net-name nil) (min ?qw74&:(<= ?qw74 ?vcom)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?qz53&:(and (<= ?qz53 ?vvmax) (> ?qz53 ?hcom)))))
  (not (vertical (status nil) (net-name ?nn&~nil) (com ?qz38&:(and (> ?qz38 ?vcom) (< ?qz38 ?gx)))))
  (not (horizontal (status nil) (net-name ?nn&~nil) (com ?qz39&:(and (> ?qz39 ?vmax) (< ?qz39 ?hcom)))))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw75&:(<= ?qw75 ?vcom)) (max ?qw62&:(>= ?qw62 ?vcom)) (com ?hcom) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw76&:(<= ?qw76 ?hcom)) (max ?qw63&:(>= ?qw63 ?hcom)) (com ?vcom) (layer ?lay)))
  =>
  (modify ?c6 (min ?hcom) (min-net ?nn))
  (modify ?c4 (max ?hcom))
  (assert (pull-ff south ?nn ?gx ?gy ?gx ?hcom))
  (assert (ff (net-name ?nn) (pin-name ?pn) (grid-layer ?lay) (grid-x ?vcom) (grid-y ?hcom) (came-from south)))
  (modify ?f1 (present find-no-of-pins-on-a-row-col))
)

(defrule p456
  ?f1 <- (context (present extend-total-verti))
  ?c2 <- (total-verti ?nn top ? 1)
  (ff (came-from north) (net-name ?nn) (grid-x ?gx) (grid-y ?gy))
  ?c4 <- (vertical (status nil) (net-name ?nn&~nil) (min ?garb1) (max ?vmax&:(< ?vmax ?gy)) (com ?vcom&:(< ?vcom ?gx)) (layer ?lay) (compo ?garb2) (commo ?garb3) (pin-name ?pn))
  (not (vertical-s (net-name ~?nn) (com ?vcom)))
  ?c5 <- (ff (net-name ?nn) (grid-x ?vcom) (grid-y ?vmax) (grid-layer ?garb4) (pin-name ?garb5))
  ?c7 <- (vertical (net-name nil) (min ?vmax) (max ?vvmax&:(> ?vvmax ?vmax)) (com ?vcom) (layer ?lay) (compo ?garb6) (commo ?garb7))
  (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?vcom)) (max ?qz65&:(and (> ?qz65 ?hmin) (>= ?qz65 ?gx))) (com ?hcom&:(and (<= ?hcom ?vvmax) (> ?hcom ?vmax))))
  (not (horizontal (net-name nil) (min ?qw74&:(<= ?qw74 ?vcom)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?qz53&:(and (<= ?qz53 ?vvmax) (> ?qz53 ?hcom)))))
  (not (vertical (status nil) (net-name ?nn&~nil) (com ?qz38&:(and (> ?qz38 ?vcom) (< ?qz38 ?gx)))))
  (not (horizontal (status nil) (net-name ?nn&~nil) (com ?qz39&:(and (> ?qz39 ?vmax) (< ?qz39 ?hcom)))))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw75&:(<= ?qw75 ?vcom)) (max ?qw63&:(>= ?qw63 ?vcom)) (com ?hcom) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw76&:(<= ?qw76 ?hcom)) (max ?qw64&:(>= ?qw64 ?hcom)) (com ?vcom) (layer ?lay)))
  =>
  (modify ?c7 (min ?hcom) (min-net ?nn))
  (modify ?c4 (max ?hcom))
  (assert (pull-ff south ?nn ?gx ?gy ?gx ?hcom))
  (modify ?c5 (grid-y ?hcom) (grid-layer ?lay))
  (modify ?f1 (present find-no-of-pins-on-a-row-col))
)
