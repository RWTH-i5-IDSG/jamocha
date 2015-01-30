

(defrule p591
  (move-ff ?nn ? ?)
  (horizontal (net-name ?nn) (min ?hmin1) (max ?hmax1) (com ?hcom1))
  (horizontal (net-name ?nn) (min ?hmin2&:(>= ?hmin2 ?hmin1)) (max ?hmax2&:(<= ?hmax2 ?hmax1)) (com ?hcom2&:(< ?hcom2 ?hcom1)))
  ?v <- (vertical-s (net-name ?nn) (min ?vmin&:(<= ?vmin ?hcom2)) (max ?vmax&:(>= ?vmax ?hcom1)) (com ?vcom&:(and (>= ?vcom ?hmin2) (<= ?vcom ?hmax2))) (id ?id))
  (vertical (net-name nil) (min ?qw74&:(<= ?qw74 ?hcom2)) (max ?qw62&:(>= ?qw62 ?hcom1)) (com ?vcom2&:(and (>= ?vcom2 ?hmin2) (<= ?vcom2 ?hmax2))))
  (not (vertical-s (net-name ~?nn) (com ?vcom2)))
  =>
  (retract ?v)
  (assert (next-segment ?nn col ?vcom2 nil ?vmin ?vmax))
)

(defrule p592
  (move-ff ?nn ? ?)
  (horizontal (net-name ?nn) (min ?hmin1) (max ?hmax1) (com ?hcom1))
  (horizontal (net-name ?nn) (min ?hmin2&:(>= ?hmin2 ?hmin1)) (max ?hmax2&:(<= ?hmax2 ?hmax1)) (com ?hcom2&:(> ?hcom2 ?hcom1)))
  ?v <- (vertical-s (net-name ?nn) (min ?vmin&:(<= ?vmin ?hcom1)) (max ?vmax&:(>= ?vmax ?hcom2)) (com ?vcom&:(and (>= ?vcom ?hmin2) (<= ?vcom ?hmax2))) (id ?id))
  (vertical (net-name nil) (min ?qw74&:(<= ?qw74 ?hcom1)) (max ?qw62&:(>= ?qw62 ?hcom2)) (com ?vcom2&:(and (>= ?vcom2 ?hmin2) (<= ?vcom2 ?hmax2))))
  (not (vertical-s (net-name ~?nn) (com ?vcom2)))
  =>
  (retract ?v)
  (assert (next-segment ?nn col ?vcom2 nil ?vmin ?vmax))
)

(defrule p593
  (move-ff ?nn ? ?)
  (horizontal (net-name ?nn) (min ?hmin1) (max ?hmax1) (com ?hcom1))
  (horizontal (net-name ?nn) (min ?hmin2&:(and (>= ?hmin2 ?hmin1) (<= ?hmin2 ?hmax1))) (max ?hmax2&:(>= ?hmax2 ?hmax1)) (com ?hcom2&:(< ?hcom2 ?hcom1)))
  ?v <- (vertical-s (net-name ?nn) (min ?vmin&:(<= ?vmin ?hcom2)) (max ?vmax&:(>= ?vmax ?hcom1)) (com ?vcom&:(and (>= ?vcom ?hmin2) (<= ?vcom ?hmax1))) (id ?id))
  (vertical (net-name nil) (min ?qw74&:(<= ?qw74 ?hcom2)) (max ?qw62&:(>= ?qw62 ?hcom1)) (com ?vcom2&:(and (>= ?vcom2 ?hmin2) (<= ?vcom2 ?hmax1))))
  (not (vertical-s (net-name ~?nn) (com ?vcom2)))
  =>
  (retract ?v)
  (assert (next-segment ?nn col ?vcom2 nil ?vmin ?vmax))
)

(defrule p594
  (move-ff ?nn ? ?)
  (horizontal (net-name ?nn) (min ?hmin1) (max ?hmax1) (com ?hcom1))
  (horizontal (net-name ?nn) (min ?hmin2&:(and (>= ?hmin2 ?hmin1) (<= ?hmin2 ?hmax1))) (max ?hmax2&:(>= ?hmax2 ?hmax1)) (com ?hcom2&:(> ?hcom2 ?hcom1)))
  ?v <- (vertical-s (net-name ?nn) (min ?vmin&:(<= ?vmin ?hcom1)) (max ?vmax&:(>= ?vmax ?hcom2)) (com ?vcom&:(and (>= ?vcom ?hmin2) (<= ?vcom ?hmax1))) (id ?id))
  (vertical (net-name nil) (min ?qw74&:(<= ?qw74 ?hcom1)) (max ?qw62&:(>= ?qw62 ?hcom2)) (com ?vcom2&:(and (>= ?vcom2 ?hmin2) (<= ?vcom2 ?hmax1))))
  (not (vertical-s (net-name ~?nn) (com ?vcom2)))
  =>
  (retract ?v)
  (assert (next-segment ?nn col ?vcom2 nil ?vmin ?vmax))
)

(defrule p595
  (move-ff ?nn ? ?)
  (vertical (net-name ?nn) (min ?min) (max ?max) (com ?com))
  (ff (net-name ?nn) (grid-x ?gx&:(< ?gx ?com)) (grid-y ?gy&:(and (>= ?gy ?min) (<= ?gy ?max))))
  ?v <- (horizontal-s (net-name ?nn) (min ?vmin&:(<= ?vmin ?gx)) (max ?vmax&:(>= ?vmax ?com)) (com ?gy) (id ?id))
  (horizontal (net-name nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw62&:(>= ?qw62 ?com)) (com ?gy))
  (not (horizontal-s (net-name ~?nn) (com ?gy)))
  =>
  (retract ?v)
  (assert (next-segment ?nn row ?gy nil ?vmin ?vmax))
)

(defrule p596
  (move-ff ?nn ? ?)
  (vertical (net-name ?nn) (min ?min) (max ?max) (com ?com))
  (ff (net-name ?nn) (grid-x ?gx&:(> ?gx ?com)) (grid-y ?gy&:(and (>= ?gy ?min) (<= ?gy ?max))))
  ?v <- (horizontal-s (net-name ?nn) (min ?vmin&:(<= ?vmin ?com)) (max ?vmax&:(>= ?vmax ?gx)) (com ?gy) (id ?id))
  (horizontal (net-name nil) (min ?qw74&:(<= ?qw74 ?com)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy))
  (not (horizontal-s (net-name ~?nn) (com ?gy)))
  =>
  (retract ?v)
  (assert (next-segment ?nn row ?gy nil ?vmin ?vmax))
)

(defrule p597
  (move-ff ?nn ? ?)
  (vertical (net-name ?nn) (min ?hmin1) (max ?hmax1) (com ?hcom1))
  (vertical (net-name ?nn) (min ?hmin2&:(>= ?hmin2 ?hmin1)) (max ?hmax2&:(<= ?hmax2 ?hmax1)) (com ?hcom2&:(< ?hcom2 ?hcom1)))
  ?v <- (horizontal-s (net-name ?nn) (min ?vmin&:(<= ?vmin ?hcom2)) (max ?vmax&:(>= ?vmax ?hcom1)) (com ?vcom&:(and (>= ?vcom ?hmin2) (<= ?vcom ?hmax2))) (id ?id))
  (horizontal (net-name nil) (min ?qw74&:(<= ?qw74 ?hcom2)) (max ?qw62&:(>= ?qw62 ?hcom1)) (com ?vcom2&:(and (>= ?vcom2 ?hmin2) (<= ?vcom2 ?hmax2))))
  (not (horizontal-s (net-name ~?nn) (com ?vcom2)))
  =>
  (retract ?v)
  (assert (next-segment ?nn row ?vcom2 nil ?vmin ?vmax))
)

(defrule p598
  (move-ff ?nn ? ?)
  (vertical (net-name ?nn) (min ?hmin1) (max ?hmax1) (com ?hcom1))
  (vertical (net-name ?nn) (min ?hmin2&:(>= ?hmin2 ?hmin1)) (max ?hmax2&:(<= ?hmax2 ?hmax1)) (com ?hcom2&:(> ?hcom2 ?hcom1)))
  ?v <- (horizontal-s (net-name ?nn) (min ?vmin&:(<= ?vmin ?hcom1)) (max ?vmax&:(>= ?vmax ?hcom2)) (com ?vcom&:(and (>= ?vcom ?hmin2) (<= ?vcom ?hmax2))) (id ?id))
  (horizontal (net-name nil) (min ?qw74&:(<= ?qw74 ?hcom1)) (max ?qw62&:(>= ?qw62 ?hcom2)) (com ?vcom2&:(and (>= ?vcom2 ?hmin2) (<= ?vcom2 ?hmax2))))
  (not (horizontal-s (net-name ~?nn) (com ?vcom2)))
  =>
  (retract ?v)
  (assert (next-segment ?nn row ?vcom2 nil ?vmin ?vmax))
)

(defrule p599
  (move-ff ?nn ? ?)
  (vertical (net-name ?nn) (min ?hmin1) (max ?hmax1) (com ?hcom1))
  (vertical (net-name ?nn) (min ?hmin2&:(and (>= ?hmin2 ?hmin1) (<= ?hmin2 ?hmax1))) (max ?hmax2&:(>= ?hmax2 ?hmax1)) (com ?hcom2&:(< ?hcom2 ?hcom1)))
  ?v <- (horizontal-s (net-name ?nn) (min ?vmin&:(<= ?vmin ?hcom2)) (max ?vmax&:(>= ?vmax ?hcom1)) (com ?vcom&:(and (>= ?vcom ?hmin2) (<= ?vcom ?hmax1))) (id ?id))
  (horizontal (net-name nil) (min ?qw74&:(<= ?qw74 ?hcom2)) (max ?qw62&:(>= ?qw62 ?hcom1)) (com ?vcom2&:(and (>= ?vcom2 ?hmin2) (<= ?vcom2 ?hmax1))))
  (not (horizontal-s (net-name ~?nn) (com ?vcom2)))
  =>
  (retract ?v)
  (assert (next-segment ?nn row ?vcom2 nil ?vmin ?vmax))
)

(defrule p600
  (move-ff ?nn ? ?)
  (vertical (net-name ?nn) (min ?hmin1) (max ?hmax1) (com ?hcom1))
  (vertical (net-name ?nn) (min ?hmin2&:(and (>= ?hmin2 ?hmin1) (<= ?hmin2 ?hmax1))) (max ?hmax2&:(>= ?hmax2 ?hmax1)) (com ?hcom2&:(> ?hcom2 ?hcom1)))
  ?v <- (horizontal-s (net-name ?nn) (min ?vmin&:(<= ?vmin ?hcom1)) (max ?vmax&:(>= ?vmax ?hcom2)) (com ?vcom&:(and (>= ?vcom ?hmin2) (<= ?vcom ?hmax1))) (id ?id))
  (horizontal (net-name nil) (min ?qw74&:(<= ?qw74 ?hcom1)) (max ?qw62&:(>= ?qw62 ?hcom2)) (com ?vcom2&:(and (>= ?vcom2 ?hmin2) (<= ?vcom2 ?hmax1))))
  (not (horizontal-s (net-name ~?nn) (com ?vcom2)))
  =>
  (retract ?v)
  (assert (next-segment ?nn row ?vcom2 nil ?vmin ?vmax))
)

(defrule p601
  ?m <- (move-ff ? ? ?)
  =>
  (retract ?m)
)



(defrule p602
  ?e <- (end-of-specialized-move-ff)
  (not (next-segment ? ? ? ? ? ?))
  =>
  (retract ?e)
  (assert (context (present check-for-routed-net)))
)

(defrule p603
  (context (present check-for-routed-net | propagate-constraint))
  ?f2 <- (dominant-layer | change-priority | next-segment $?)
  =>
  (retract ?f2)
)

(defrule p244
  (context (present lshape1))
  ?f2 <- (total-verti | counted-verti | total | counted $?)
  =>
  (retract ?f2)
)

(defrule p245
  (context (present lshape1))
  (last-row ?gy)
  ?f3 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?pn) (came-from north))
  (not (ff (net-name ?nn) (grid-y ?gy) (pin-name ~?pn)))
  (ff (net-name ?nn) (grid-x ?gx2&:(> ?gx2 ?gx)) (grid-y ?gy2))
  (not (vertical (com ?qz38&:(and (> ?qz38 ?gx) (< ?qz38 ?gx2)))))
  ?f5 <- (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?gx)) (max ?hmax&:(and (> ?hmax ?hmin) (>= ?hmax ?gx2))) (com ?gy) (layer ?lay) (commo ?hcmo) (compo ?hcpo) (min-net ?mn))
  (not (pin (pin-x ?gx2) (pin-y ?hcpo)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw62&:(>= ?qw62 ?gy)) (com ?gx2) (layer ?lay)))
  (not (horizontal (status nil) (net-name ~nil&~?nn) (min ?qw37&:(<= ?qw37 ?gx2)) (max ?qw63&:(>= ?qw63 ?gx2)) (com ?gy) (layer ?lay)))
  =>
  (assert (horizontal (layer ?lay) (min ?hmin) (max ?gx) (min-net ?mn) (max-net ?nn) (com ?gy) (commo ?hcmo) (compo ?hcpo)))
  (modify ?f5 (min ?gx2) (min-net ?nn))
  (assert (horizontal (net-name ?nn) (pin-name ?pn) (layer ?lay) (min ?gx) (max ?gx2) (com ?gy) (commo ?hcmo) (compo ?hcpo)))
  (modify ?f3 (grid-x ?gx2) (came-from west))
)

(defrule p246
  (context (present lshape1))
  (last-row ?gy)
  ?f3 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?pn) (came-from north))
  (not (ff (net-name ?nn) (grid-y ?gy) (pin-name ~?pn)))
  (ff (net-name ?nn) (grid-x ?gx2&:(< ?gx2 ?gx)) (grid-y ?gy2))
  (not (vertical (com ?qz38&:(and (> ?qz38 ?gx2) (< ?qz38 ?gx)))))
  ?f5 <- (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?gx2)) (max ?hmax&:(and (> ?hmax ?hmin) (>= ?hmax ?gx))) (com ?gy) (layer ?lay) (compo ?hcpo) (commo ?hcmo) (min-net ?mn))
  (not (pin (pin-x ?gx2) (pin-y ?hcpo)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw63&:(>= ?qw63 ?gy)) (com ?gx2) (layer ?lay)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw37&:(<= ?qw37 ?gx2)) (max ?qw64&:(>= ?qw64 ?gx2)) (com ?gy) (layer ?lay)))
  =>
  (assert (horizontal (layer ?lay) (min ?hmin) (max ?gx2) (min-net ?mn) (max-net ?nn) (com ?gy) (commo ?hcmo) (compo ?hcpo)))
  (modify ?f5 (min ?gx) (min-net ?nn))
  (assert (horizontal (net-name ?nn) (pin-name ?pn) (layer ?lay) (min ?gx2) (max ?gx) (com ?gy) (commo ?hcmo) (compo ?hcpo)))
  (modify ?f3 (grid-x ?gx2) (came-from east))
)

(defrule p247
  (context (present lshape1))
  ?f2 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y 1) (grid-layer ?lay) (pin-name ?pn) (came-from south))
  (not (ff (net-name ?nn) (grid-y 1) (pin-name ~?pn)))
  (ff (net-name ?nn) (grid-x ?gx2&:(> ?gx2 ?gx)) (grid-y ?gy2))
  (not (vertical (com ?qz38&:(and (> ?qz38 ?gx) (< ?qz38 ?gx2)))))
  ?f4 <- (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?gx)) (max ?hmax&:(and (> ?hmax ?hmin) (>= ?hmax ?gx2))) (com 1) (layer ?lay) (compo ?hcpo) (commo ?hcmo) (min-net ?mn))
  (not (pin (pin-x ?gx2) (pin-y 0)))
  (not (vertical (net-name ~?nn&~nil) (min ?qw74&:(<= ?qw74 1)) (max ?qw62&:(>= ?qw62 1)) (com ?gx2) (layer ?lay)))
  (not (horizontal (net-name ~?nn&~nil) (min ?qw37&:(<= ?qw37 ?gx2)) (max ?qw63&:(>= ?qw63 ?gx2)) (com 1) (layer ?lay)))
  =>
  (assert (horizontal (layer ?lay) (min ?hmin) (max ?gx) (min-net ?mn) (max-net ?nn) (com 1) (commo ?hcmo) (compo ?hcpo)))
  (modify ?f4 (min ?gx2) (min-net ?nn))
  (assert (horizontal (net-name ?nn) (pin-name ?pn) (layer ?lay) (min ?gx) (max ?gx2) (com 1) (commo ?hcmo) (compo ?hcpo)))
  (modify ?f2 (grid-x ?gx2) (came-from west))
)

(defrule p248
  (context (present lshape1))
  ?f2 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y 1) (grid-layer ?lay) (pin-name ?pn) (came-from south))
  (not (ff (net-name ?nn) (grid-y 1) (pin-name ~?pn)))
  (ff (net-name ?nn) (grid-x ?gx2&:(< ?gx2 ?gx)) (grid-y ?gy2))
  (not (vertical (com ?qz38&:(and (> ?qz38 ?gx2) (< ?qz38 ?gx)))))
  ?f4 <- (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?gx2)) (max ?hmax&:(and (> ?hmax ?hmin) (>= ?hmax ?gx))) (com 1) (layer ?lay) (compo ?hcpo) (commo ?hcmo) (min-net ?mn))
  (not (pin (pin-x ?gx2) (pin-y 0)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw74&:(<= ?qw74 1)) (max ?qw62&:(>= ?qw62 1)) (com ?gx2) (layer ?lay)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw37&:(<= ?qw37 ?gx2)) (max ?qw63&:(>= ?qw63 ?gx2)) (com 1) (layer ?lay)))
  =>
  (assert (horizontal (layer ?lay) (min ?hmin) (max ?gx2) (min-net ?mn) (max-net ?nn) (com 1) (commo ?hcmo) (compo ?hcpo)))
  (modify ?f4 (min ?gx) (min-net ?nn))
  (assert (horizontal (net-name ?nn) (pin-name ?pn) (layer ?lay) (min ?gx2) (max ?gx) (com 1) (commo ?hcmo) (compo ?hcpo)))
  (modify ?f2 (grid-x ?gx2) (came-from east))
)

(defrule p249
  (context (present lshape1))
  (last-col ?gx)
  ?f3 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?pn) (came-from east))
  (not (ff (net-name ?nn) (grid-x ?gx) (pin-name ~?pn)))
  (ff (net-name ?nn) (grid-x ?gx2) (grid-y ?gy2&:(> ?gy2 ?gy)))
  (not (horizontal (com ?qz38&:(and (> ?qz38 ?gy) (< ?qz38 ?gy2)))))
  ?f5 <- (vertical (net-name nil) (min ?vmin&:(<= ?vmin ?gy)) (max ?vmax&:(and (>= ?vmax ?gy2) (> ?vmax ?vmin))) (com ?gx) (layer ?lay) (compo ?vcpo) (commo ?vcmo) (min-net ?mn))
  (not (pin (pin-x ?vcpo) (pin-y ?gy2)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw31&:(<= ?qw31 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy2) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw74&:(<= ?qw74 ?gy2)) (max ?qw62&:(>= ?qw62 ?gy2)) (com ?gx) (layer ?lay)))
  =>
  (assert (vertical (layer ?lay) (min ?vmin) (max ?gy) (min-net ?mn) (max-net ?nn) (com ?gx) (commo ?vcmo) (compo ?vcpo)))
  (modify ?f5 (min ?gy2) (min-net ?nn))
  (assert (vertical (net-name ?nn) (pin-name ?pn) (layer ?lay) (min ?gy) (max ?gy2) (com ?gx) (commo ?vcmo) (compo ?vcpo)))
  (modify ?f3 (grid-y ?gy2) (came-from south))
)

(defrule p250
  (context (present lshape1))
  (last-col ?gx)
  ?f3 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?pn) (came-from east))
  (not (ff (net-name ?nn) (grid-x ?gx) (pin-name ~?pn)))
  (ff (net-name ?nn) (grid-x ?gx2) (grid-y ?gy2&:(< ?gy2 ?gy)))
  (not (horizontal (com ?qz38&:(and (> ?qz38 ?gy2) (< ?qz38 ?gy)))))
  ?f5 <- (vertical (net-name nil) (min ?vmin&:(<= ?vmin ?gy2)) (max ?vmax&:(and (>= ?vmax ?gy) (> ?vmax ?vmin))) (com ?gx) (layer ?lay) (compo ?vcpo) (commo ?vcmo) (min-net ?mn))
  (not (pin (pin-x ?vcpo) (pin-y ?gy2)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy2) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw74&:(<= ?qw74 ?gy2)) (max ?qw62&:(>= ?qw62 ?gy2)) (com ?gx) (layer ?lay)))
  =>
  (assert (vertical (layer ?lay) (min ?vmin) (max ?gy2) (min-net ?mn) (max-net ?nn) (com ?gx) (commo ?vcmo) (compo ?vcpo)))
  (modify ?f5 (min ?gy) (min-net ?nn))
  (assert (vertical (net-name ?nn) (pin-name ?pn) (layer ?lay) (min ?gy2) (max ?gy) (com ?gx) (commo ?vcmo) (compo ?vcpo)))
  (modify ?f3 (grid-y ?gy2) (came-from north))
)

(defrule p251
  (context (present lshape1))
  ?f2 <- (ff (net-name ?nn) (grid-x 1) (grid-y ?gy) (grid-layer ?lay) (pin-name ?pn) (came-from west))
  (not (ff (net-name ?nn) (grid-x 1) (pin-name ~?pn)))
  (ff (net-name ?nn) (grid-x ?gx2) (grid-y ?gy2&:(> ?gy2 ?gy)))
  (not (horizontal (com ?qz38&:(and (> ?qz38 ?gy) (< ?qz38 ?gy2)))))
  ?f4 <- (vertical (net-name nil) (min ?vmin&:(<= ?vmin ?gy)) (max ?vmax&:(and (> ?vmax ?vmin) (>= ?vmax ?gy2))) (com 1) (layer ?lay) (compo ?vcpo) (commo ?vcmo) (min-net ?mn))
  (not (pin (pin-x 0) (pin-y ?gy2)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw74&:(<= ?qw74 1)) (max ?qw62&:(>= ?qw62 1)) (com ?gy2) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw75&:(<= ?qw75 ?gy2)) (max ?qw63&:(>= ?qw63 ?gy2)) (com 1) (layer ?lay)))
  =>
  (assert (vertical (layer ?lay) (min ?vmin) (max ?gy) (min-net ?mn) (max-net ?nn) (com 1) (commo ?vcmo) (compo ?vcpo)))
  (modify ?f4 (min ?gy2) (min-net ?nn))
  (assert (vertical (net-name ?nn) (pin-name ?pn) (layer ?lay) (min ?gy) (max ?gy2) (com 1) (commo ?vcmo) (compo ?vcpo)))
  (modify ?f2 (grid-y ?gy2) (came-from south))
)

(defrule p252
  (context (present lshape1))
  ?f2 <- (ff (net-name ?nn) (grid-x 1) (grid-y ?gy) (grid-layer ?lay) (pin-name ?pn) (came-from west))
  (not (ff (net-name ?nn) (grid-x 1) (pin-name ~?pn)))
  (ff (net-name ?nn) (grid-x ?gx2) (grid-y ?gy2&:(< ?gy2 ?gy)))
  (not (horizontal (com ?qz38&:(and (> ?qz38 ?gy2) (< ?qz38 ?gy)))))
  ?f4 <- (vertical (net-name nil) (min ?vmin&:(<= ?vmin ?gy2)) (max ?vmax&:(and (> ?vmax ?vmin) (>= ?vmax ?gy))) (com 1) (layer ?lay) (compo ?vcpo) (commo ?vcmo) (min-net ?mn))
  (not (pin (pin-x 0) (pin-y ?gy2)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw74&:(<= ?qw74 1)) (max ?qw62&:(>= ?qw62 1)) (com ?gy2) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw75&:(<= ?qw75 ?gy2)) (max ?qw63&:(>= ?qw63 ?gy2)) (com 1) (layer ?lay)))
  =>
  (assert (vertical (layer ?lay) (min ?vmin) (max ?gy2) (min-net ?mn) (max-net ?nn) (com 1) (commo ?vcmo) (compo ?vcpo)))
  (modify ?f4 (min ?gy) (min-net ?nn))
  (assert (vertical (net-name ?nn) (pin-name ?pn) (layer ?lay) (min ?gy2) (max ?gy) (com 1) (commo ?vcmo) (compo ?vcpo)))
  (modify ?f2 (grid-y ?gy2) (came-from north))
)

(defrule p253
  ?f1 <- (context (present lshape1))
  (net (net-name ?nn) (net-no-of-pins 2) (net-is-routed ~yes))
  ?f3 <- (ff (net-name ?nn) (grid-x ?gx1) (grid-y ?gy1) (grid-layer ?vlay) (pin-name ?pn))
  ?f4 <- (ff (net-name ?nn) (grid-x ?gx2&:(> ?gx2 ?gx1)) (grid-y ?gy2&:(< ?gy2 ?gy1)) (grid-layer ?hlay))
  ?f5 <- (vertical (net-name nil) (min ?vmin&:(<= ?vmin ?gy2)) (max ?vmax&:(and (>= ?vmax ?gy1) (> ?vmax ?vmin))) (com ?gx1) (layer ?vlay) (compo ?vcpo) (commo ?vcmo) (min-net ?vnn))
  ?f6 <- (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?gx1)) (max ?hmax&:(and (>= ?hmax ?gx2) (> ?hmax ?hmin))) (com ?gy2) (layer ?hlay) (compo ?hcpo) (commo ?hcmo) (min-net ?hnn))
  (vertical-layer ?vlay ?)
  (horizontal-layer ?hlay ?)
  (not (vertical (status nil) (net-name ~?nn&~nil) (max ?qw74&:(<= ?qw74 ?vmin)) (com ?gx1)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw62&:(>= ?qw62 ?vmax)) (com ?gx1)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (max ?qw75&:(<= ?qw75 ?hmin)) (com ?gy2)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw63&:(>= ?qw63 ?hmax)) (com ?gy2)))
  =>
  (retract ?f1 ?f3 ?f4)
  (assert (vertical (com ?gx1) (layer ?vlay) (min ?vmin) (max ?gy2) (compo ?vcpo) (commo ?vcmo) (min-net ?vnn) (max-net ?nn)))
  (modify ?f5 (min ?gy1) (min-net ?nn))
  (assert (horizontal (com ?gy2) (layer ?hlay) (min ?hmin) (max ?gx1) (compo ?hcpo) (commo ?hcmo) (min-net ?hnn) (max-net ?nn)))
  (modify ?f6 (min ?gx2) (min-net ?nn))
  (assert (vertical (com ?gx1) (layer ?vlay) (min ?gy2) (max ?gy1) (compo ?vcpo) (commo ?vcmo) (net-name ?nn) (pin-name ?pn)))
  (assert (horizontal (com ?gy2) (layer ?hlay) (min ?gx1) (max ?gx2) (compo ?hcpo) (commo ?hcmo) (net-name ?nn) (pin-name ?pn)))
  (assert (context (present check-for-routed-net)))
)

(defrule p254
  ?f1 <- (context (present lshape1))
  (net (net-name ?nn) (net-no-of-pins 2) (net-is-routed ~yes))
  ?f3 <- (ff (net-name ?nn) (grid-x ?gx1) (grid-y ?gy1) (grid-layer ?vlay) (pin-name ?pn))
  ?f4 <- (ff (net-name ?nn) (grid-x ?gx2&:(< ?gx2 ?gx1)) (grid-y ?gy2&:(< ?gy2 ?gy1)) (grid-layer ?hlay))
  ?f5 <- (vertical (net-name nil) (min ?vmin&:(<= ?vmin ?gy2)) (max ?vmax&:(and (> ?vmax ?vmin) (>= ?vmax ?gy1))) (com ?gx1) (layer ?vlay) (compo ?vcpo) (commo ?vcmo) (min-net ?vnn))
  ?f6 <- (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?gx2)) (max ?hmax&:(and (> ?hmax ?hmin) (>= ?hmax ?gx1))) (com ?gy2) (layer ?hlay) (compo ?hcpo) (commo ?hcmo) (min-net ?hnn))
  (vertical-layer ?vlay ?)
  (horizontal-layer ?hlay ?)
  (not (vertical (status nil) (net-name ~?nn&~nil) (max ?qw74&:(<= ?qw74 ?vmin)) (com ?gx1)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw62&:(>= ?qw62 ?vmax)) (com ?gx1)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (max ?qw75&:(<= ?qw75 ?hmin)) (com ?gy2)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw63&:(>= ?qw63 ?hmax)) (com ?gy2)))
  =>
  (retract ?f1 ?f3 ?f4)
  (assert (vertical (com ?gx1) (layer ?vlay) (min ?vmin) (max ?gy2) (compo ?vcpo) (commo ?vcmo) (min-net ?vnn) (max-net ?nn)))
  (modify ?f5 (min ?gy1) (min-net ?nn))
  (assert (horizontal (com ?gy2) (layer ?hlay) (min ?hmin) (max ?gx2) (compo ?hcpo) (commo ?hcmo) (min-net ?hnn) (max-net ?nn)))
  (modify ?f6 (min ?gx1) (min-net ?nn))
  (assert (vertical (com ?gx1) (layer ?vlay) (min ?gy2) (max ?gy1) (compo ?vcpo) (commo ?vcmo) (net-name ?nn) (pin-name ?pn)))
  (assert (horizontal (com ?gy2) (layer ?hlay) (min ?gx2) (max ?gx1) (compo ?hcpo) (commo ?hcmo) (net-name ?nn) (pin-name ?pn)))
  (assert (context (present check-for-routed-net)))
)

(defrule p255
  ?f1 <- (context (present lshape1))
  (net (net-name ?nn) (net-no-of-pins 2) (net-is-routed ~yes))
  ?f3 <- (ff (net-name ?nn) (grid-x ?gx1) (grid-y ?gy1) (grid-layer ?vlay) (pin-name ?pn))
  ?f4 <- (ff (net-name ?nn) (grid-x ?gx2&:(> ?gx2 ?gx1)) (grid-y ?gy2&:(> ?gy2 ?gy1)) (grid-layer ?hlay))
  ?f5 <- (vertical (net-name nil) (min ?vmin&:(<= ?vmin ?gy1)) (max ?vmax&:(and (> ?vmax ?vmin) (>= ?vmax ?gy2))) (com ?gx1) (layer ?vlay) (compo ?vcpo) (commo ?vcmo) (min-net ?vnn))
  ?f6 <- (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?gx1)) (max ?hmax&:(and (> ?hmax ?hmin) (>= ?hmax ?gx2))) (com ?gy2) (layer ?hlay) (compo ?hcpo) (commo ?hcmo) (min-net ?hnn))
  (vertical-layer ?vlay ?)
  (horizontal-layer ?hlay ?)
  (not (vertical (status nil) (net-name ~?nn&~nil) (max ?qw74&:(<= ?qw74 ?vmin)) (com ?gx1)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw62&:(>= ?qw62 ?vmax)) (com ?gx1)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (max ?qw75&:(<= ?qw75 ?hmin)) (com ?gy2)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw63&:(>= ?qw63 ?hmax)) (com ?gy2)))
  =>
  (retract ?f1 ?f3 ?f4)
  (assert (vertical (com ?gx1) (layer ?vlay) (min ?vmin) (max ?gy1) (compo ?vcpo) (commo ?vcmo) (min-net ?vnn) (max-net ?nn)))
  (modify ?f5 (min ?gy2) (min-net ?nn))
  (assert (horizontal (com ?gy2) (layer ?hlay) (min ?hmin) (max ?gx1) (compo ?hcpo) (commo ?hcmo) (min-net ?hnn) (max-net ?nn)))
  (modify ?f6 (min ?gx2) (min-net ?nn))
  (assert (vertical (com ?gx1) (layer ?vlay) (min ?gy1) (max ?gy2) (compo ?vcpo) (commo ?vcmo) (net-name ?nn) (pin-name ?pn)))
  (assert (horizontal (com ?gy2) (layer ?hlay) (min ?gx1) (max ?gx2) (compo ?hcpo) (commo ?hcmo) (net-name ?nn) (pin-name ?pn)))
  (assert (context (present check-for-routed-net)))
)

(defrule p256
  ?f1 <- (context (present lshape1))
  (net (net-name ?nn) (net-no-of-pins 2) (net-is-routed ~yes))
  ?f3 <- (ff (net-name ?nn) (grid-x ?gx1) (grid-y ?gy1) (grid-layer ?hlay) (pin-name ?pn))
  ?f4 <- (ff (net-name ?nn) (grid-x ?gx2&:(> ?gx2 ?gx1)) (grid-y ?gy2&:(< ?gy2 ?gy1)) (grid-layer ?vlay))
  ?f5 <- (vertical (net-name nil) (min ?vmin&:(<= ?vmin ?gy2)) (max ?vmax&:(and (> ?vmax ?vmin) (>= ?vmax ?gy1))) (com ?gx2) (layer ?vlay) (compo ?vcpo) (commo ?vcmo) (min-net ?vnn))
  ?f6 <- (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?gx1)) (max ?hmax&:(and (> ?hmax ?hmin) (>= ?hmax ?gx2))) (com ?gy1) (layer ?hlay) (compo ?hcpo) (commo ?hcmo) (min-net ?hnn))
  (vertical-layer ?vlay ?)
  (horizontal-layer ?hlay ?)
  (not (vertical (status nil) (net-name ~?nn&~nil) (max ?qw74&:(<= ?qw74 ?vmin)) (com ?gx2)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw62&:(>= ?qw62 ?vmax)) (com ?gx2)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (max ?qw75&:(<= ?qw75 ?hmin)) (com ?gy1)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw63&:(>= ?qw63 ?hmax)) (com ?gy1)))
  =>
  (retract ?f1 ?f3 ?f4)
  (assert (vertical (com ?gx2) (layer ?vlay) (min ?vmin) (max ?gy2) (compo ?vcpo) (commo ?vcmo) (min-net ?vnn) (max-net ?nn)))
  (modify ?f5 (min ?gy1) (min-net ?nn))
  (assert (horizontal (com ?gy1) (layer ?hlay) (min ?hmin) (max ?gx1) (compo ?hcpo) (commo ?hcmo) (min-net ?hnn) (max-net ?nn)))
  (modify ?f6 (min ?gx2) (min-net ?nn))
  (assert (vertical (com ?gx2) (layer ?vlay) (min ?gy2) (max ?gy1) (compo ?vcpo) (commo ?vcmo) (net-name ?nn) (pin-name ?pn)))
  (assert (horizontal (com ?gy1) (layer ?hlay) (min ?gx1) (max ?gx2) (compo ?hcpo) (commo ?hcmo) (net-name ?nn) (pin-name ?pn)))
  (assert (context (present check-for-routed-net)))
)




(defrule p257
  ?f1 <- (context (present lshape1))
  (net (net-name ?nn) (net-no-of-pins 2) (net-is-routed ~yes))
  ?f3 <- (ff (net-name ?nn) (grid-x ?gx1) (grid-y ?gy1) (grid-layer ?vlay) (pin-name ?pn))
  ?f4 <- (ff (net-name ?nn) (grid-x ?gx2&:(> ?gx2 ?gx1)) (grid-y ?gy2&:(< ?gy2 ?gy1)) (grid-layer ?hlay))
  ?f5 <- (vertical (net-name nil) (min ?vmin&:(<= ?vmin ?gy2)) (max ?vmax&:(and (> ?vmax ?vmin) (>= ?vmax ?gy1))) (com ?gx1) (layer ?vlay) (compo ?vcpo) (commo ?vcmo) (min-net ?vnn))
  ?f6 <- (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?gx1)) (max ?hmax&:(and (> ?hmax ?hmin) (>= ?hmax ?gx2))) (com ?gy2) (layer ?hlay) (compo ?hcpo) (commo ?hcmo) (min-net ?hnn))
  (vertical-layer ?vlay ?)
  (horizontal-layer ?hlay ?)
  (not (vertical (status nil) (net-name ~?nn&~nil) (max ?qw74&:(<= ?qw74 ?vmin)) (com ?gx1)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw62&:(>= ?qw62 ?vmax)) (com ?gx1)))
  (ff (net-name ?nn1&~?nn) (grid-y ?gy2) (grid-x ?qz20&:(< ?qz20 ?gx1)))
  (not (pin (net-name ?nn1) (pin-x ?qw63&:(>= ?qw63 ?gx1))))
  =>
  (retract ?f1 ?f3 ?f4)
  (assert (vertical (com ?gx1) (layer ?vlay) (min ?vmin) (max ?gy2) (compo ?vcpo) (commo ?vcmo) (min-net ?vnn) (max-net ?nn)))
  (modify ?f5 (min ?gy1) (min-net ?nn))
  (assert (horizontal (com ?gy2) (layer ?hlay) (min ?hmin) (max ?gx1) (compo ?hcpo) (commo ?hcmo) (min-net ?hnn) (max-net ?nn)))
  (modify ?f6 (min ?gx2) (min-net ?nn))
  (assert (vertical (com ?gx1) (layer ?vlay) (min ?gy2) (max ?gy1) (compo ?vcpo) (commo ?vcmo) (net-name ?nn) (pin-name ?pn)))
  (assert (horizontal (com ?gy2) (layer ?hlay) (min ?gx1) (max ?gx2) (compo ?hcpo) (commo ?hcmo) (net-name ?nn) (pin-name ?pn)))
  (assert (context (present check-for-routed-net)))
)

(defrule p258
  ?f1 <- (context (present lshape1))
  (net (net-name ?nn) (net-no-of-pins 2) (net-is-routed ~yes))
  ?f3 <- (ff (net-name ?nn) (grid-x ?gx1) (grid-y ?gy1) (grid-layer ?vlay) (pin-name ?pn))
  ?f4 <- (ff (net-name ?nn) (grid-x ?gx2&:(< ?gx2 ?gx1)) (grid-y ?gy2&:(< ?gy2 ?gy1)) (grid-layer ?hlay))
  ?f5 <- (vertical (net-name nil) (min ?vmin&:(<= ?vmin ?gy2)) (max ?vmax&:(and (> ?vmax ?vmin) (>= ?vmax ?gy1))) (com ?gx1) (layer ?vlay) (compo ?vcpo) (commo ?vcmo) (min-net ?vnn))
  ?f6 <- (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?gx2)) (max ?hmax&:(and (> ?hmax ?hmin) (>= ?hmax ?gx1))) (com ?gy2) (layer ?hlay) (compo ?hcpo) (commo ?hcmo) (min-net ?hnn))
  (vertical-layer ?vlay ?)
  (horizontal-layer ?hlay ?)
  (not (vertical (status nil) (net-name ~?nn&~nil) (max ?qw74&:(<= ?qw74 ?vmin)) (com ?gx1)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw62&:(>= ?qw62 ?vmax)) (com ?gx1)))
  (ff (net-name ?nn1&~?nn) (grid-y ?gy2) (grid-x ?qw86&:(> ?qw86 ?gx1)))
  (not (pin (net-name ?nn1) (pin-x ?qw75&:(<= ?qw75 ?gx1))))
  =>
  (retract ?f1 ?f3 ?f4)
  (assert (vertical (com ?gx1) (layer ?vlay) (min ?vmin) (max ?gy2) (compo ?vcpo) (commo ?vcmo) (min-net ?vnn) (max-net ?nn)))
  (modify ?f5 (min ?gy1) (min-net ?nn))
  (assert (horizontal (com ?gy2) (layer ?hlay) (min ?hmin) (max ?gx2) (compo ?hcpo) (commo ?hcmo) (min-net ?hnn) (max-net ?nn)))
  (modify ?f6 (min ?gx1) (min-net ?nn))
  (assert (vertical (com ?gx1) (layer ?vlay) (min ?gy2) (max ?gy1) (compo ?vcpo) (commo ?vcmo) (net-name ?nn) (pin-name ?pn)))
  (assert (horizontal (com ?gy2) (layer ?hlay) (min ?gx2) (max ?gx1) (compo ?hcpo) (commo ?hcmo) (net-name ?nn) (pin-name ?pn)))
  (assert (context (present check-for-routed-net)))
)

(defrule p259
  ?f1 <- (context (present lshape1))
  (net (net-name ?nn) (net-no-of-pins 2) (net-is-routed ~yes))
  ?f3 <- (ff (net-name ?nn) (grid-x ?gx1) (grid-y ?gy1) (grid-layer ?vlay) (pin-name ?pn))
  ?f4 <- (ff (net-name ?nn) (grid-x ?gx2&:(> ?gx2 ?gx1)) (grid-y ?gy2&:(> ?gy2 ?gy1)) (grid-layer ?hlay))
  ?f5 <- (vertical (net-name nil) (min ?vmin&:(<= ?vmin ?gy1)) (max ?vmax&:(and (> ?vmax ?vmin) (>= ?vmax ?gy2))) (com ?gx1) (layer ?vlay) (compo ?vcpo) (commo ?vcmo) (min-net ?vnn))
  ?f6 <- (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?gx1)) (max ?hmax&:(and (> ?hmax ?hmin) (>= ?hmax ?gx2))) (com ?gy2) (layer ?hlay) (compo ?hcpo) (commo ?hcmo) (min-net ?hnn))
  (vertical-layer ?vlay ?)
  (horizontal-layer ?hlay ?)
  (not (vertical (status nil) (net-name ~?nn&~nil) (max ?qw74&:(<= ?qw74 ?vmin)) (com ?gx1)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw62&:(>= ?qw62 ?vmax)) (com ?gx1)))
  (ff (net-name ?nn1&~?nn) (grid-y ?gy2) (grid-x ?qz20&:(< ?qz20 ?gx1)))
  (not (pin (net-name ?nn1) (pin-x ?qw63&:(>= ?qw63 ?gx1))))
  =>
  (retract ?f1 ?f3 ?f4)
  (assert (vertical (com ?gx1) (layer ?vlay) (min ?vmin) (max ?gy1) (compo ?vcpo) (commo ?vcmo) (min-net ?vnn) (max-net ?nn)))
  (modify ?f5 (min ?gy2) (min-net ?nn))
  (assert (horizontal (com ?gy2) (layer ?hlay) (min ?hmin) (max ?gx1) (compo ?hcpo) (commo ?hcmo) (min-net ?hnn) (max-net ?nn)))
  (modify ?f6 (min ?gx2) (min-net ?nn))
  (assert (vertical (com ?gx1) (layer ?vlay) (min ?gy1) (max ?gy2) (compo ?vcpo) (commo ?vcmo) (net-name ?nn) (pin-name ?pn)))
  (assert (horizontal (com ?gy2) (layer ?hlay) (min ?gx1) (max ?gx2) (compo ?hcpo) (commo ?hcmo) (net-name ?nn) (pin-name ?pn)))
  (assert (context (present check-for-routed-net)))
)

(defrule p260
  ?f1 <- (context (present lshape1))
  (net (net-name ?nn) (net-no-of-pins 2) (net-is-routed ~yes))
  ?f3 <- (ff (net-name ?nn) (grid-x ?gx1) (grid-y ?gy1) (grid-layer ?hlay) (pin-name ?pn))
  ?f4 <- (ff (net-name ?nn) (grid-x ?gx2&:(> ?gx2 ?gx1)) (grid-y ?gy2&:(< ?gy2 ?gy1)) (grid-layer ?vlay))
  ?f5 <- (vertical (net-name nil) (min ?vmin&:(<= ?vmin ?gy2)) (max ?vmax&:(and (> ?vmax ?vmin) (>= ?vmax ?gy1))) (com ?gx2) (layer ?vlay) (compo ?vcpo) (commo ?vcmo) (min-net ?vnn))
  ?f6 <- (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?gx1)) (max ?hmax&:(and (> ?hmax ?hmin) (>= ?hmax ?gx2))) (com ?gy1) (layer ?hlay) (compo ?hcpo) (commo ?hcmo) (min-net ?hnn))
  (vertical-layer ?vlay ?)
  (horizontal-layer ?hlay ?)
  (not (vertical (status nil) (net-name ~?nn&~nil) (max ?qw74&:(<= ?qw74 ?vmin)) (com ?gx2)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw62&:(>= ?qw62 ?vmax)) (com ?gx2)))
  (ff (net-name ?nn1&~?nn) (grid-y ?gy1) (grid-x ?qw86&:(> ?qw86 ?gx2)))
  (not (pin (net-name ?nn1) (pin-x ?qw75&:(<= ?qw75 ?gx2))))
  =>
  (retract ?f1 ?f3 ?f4)
  (assert (vertical (com ?gx2) (layer ?vlay) (min ?vmin) (max ?gy2) (compo ?vcpo) (commo ?vcmo) (min-net ?vnn) (max-net ?nn)))
  (modify ?f5 (min ?gy1) (min-net ?nn))
  (assert (horizontal (com ?gy1) (layer ?hlay) (min ?hmin) (max ?gx1) (compo ?hcpo) (commo ?hcmo) (min-net ?hnn) (max-net ?nn)))
  (modify ?f6 (min ?gx2) (min-net ?nn))
  (assert (vertical (com ?gx2) (layer ?vlay) (min ?gy2) (max ?gy1) (compo ?vcpo) (commo ?vcmo) (net-name ?nn) (pin-name ?pn)))
  (assert (horizontal (com ?gy1) (layer ?hlay) (min ?gx1) (max ?gx2) (compo ?hcpo) (commo ?hcmo) (net-name ?nn) (pin-name ?pn)))
  (assert (context (present check-for-routed-net)))
)