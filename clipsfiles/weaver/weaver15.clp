
(defrule p561
  (next-segment ?nn row ?gy nil ?nmin ?nmax)
  ?f2 <- (ff (net-name ?nn) (grid-x ?gx1&:(and (>= ?gx1 ?nmin) (<= ?gx1 ?nmax))) (grid-y ?gy) (grid-layer ?garb1) (pin-name ?pn))
  (vertical (status nil) (net-name ?nn&~nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw63&:(>= ?qw63 ?gy)) (com ?gx2&:(and (> ?gx2 ?gx1) (<= ?gx2 ?nmax))))
  (not (ff (net-name ?nn) (grid-x ?qz38&:(and (> ?qz38 ?gx1) (< ?qz38 ?gx2))) (grid-y ?gy)))
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qw40&:(<= ?qw40 ?gy)) (max ?qw62&:(>= ?qw62 ?gy)) (com ?qz39&:(and (> ?qz39 ?gx1) (< ?qz39 ?gx2)))))
  ?f4 <- (horizontal (net-name nil) (min ?min&:(<= ?min ?gx1)) (max ?max&:(and (>= ?max ?gx2) (> ?max ?min))) (com ?gy) (layer ?lay) (compo ?cpo) (commo ?cmo) (min-net ?nn1) (max-net ?nn2))
  (horizontal-layer ?lay ?)
  (dominant-layer)
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qw34&:(<= ?qw34 ?gx1)) (max ?qw64&:(>= ?qw64 ?gx2)) (com ?gy)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw35&:(<= ?qw35 ?gx1)) (max ?qw65&:(>= ?qw65 ?gx1)) (com ?gy) (layer ?lay)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw37&:(<= ?qw37 ?gx2)) (max ?qw66&:(>= ?qw66 ?gx2)) (com ?gy) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw41&:(<= ?qw41 ?gy)) (max ?qw67&:(>= ?qw67 ?gy)) (com ?gx1) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw42&:(<= ?qw42 ?gy)) (max ?qw68&:(>= ?qw68 ?gy)) (com ?gx2) (layer ?lay)))
  =>
  (retract ?f2)
  (assert (horizontal (com ?gy) (min ?min) (max ?gx1) (compo ?cpo) (commo ?cmo) (layer ?lay) (min-net ?nn1) (max-net ?nn)))
  (assert (horizontal (com ?gy) (min ?gx2) (max ?max) (compo ?cpo) (commo ?cmo) (layer ?lay) (min-net ?nn) (max-net ?nn2)))
  (modify ?f4 (min ?gx1) (max ?gx2) (net-name ?nn) (pin-name ?pn))
)



(defrule p562
  (next-segment ?nn row ?gy nil ?nmin ?nmax)
  ?f2 <- (ff (net-name ?nn) (grid-x ?gx1&:(and (>= ?gx1 ?nmin) (<= ?gx1 ?nmax))) (grid-y ?gy) (grid-layer ?garb1) (pin-name ?pn))
  (vertical (status nil) (net-name ?nn&~nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw63&:(>= ?qw63 ?gy)) (com ?gx2&:(and (< ?gx2 ?gx1) (>= ?gx2 ?nmin))))
  (not (ff (net-name ?nn) (grid-x ?qz61&:(and (< ?qz61 ?gx1) (> ?qz61 ?gx2))) (grid-y ?gy)))
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qw40&:(<= ?qw40 ?gy)) (max ?qw62&:(>= ?qw62 ?gy)) (com ?qz62&:(and (< ?qz62 ?gx1) (> ?qz62 ?gx2)))))
  ?f4 <- (horizontal (net-name nil) (min ?min&:(<= ?min ?gx2)) (max ?max&:(and (>= ?max ?gx1) (> ?max ?min))) (com ?gy) (layer ?lay) (compo ?cpo) (commo ?cmo) (min-net ?nn1) (max-net ?nn2))
  (horizontal-layer ?lay ?)
  (dominant-layer)
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qw37&:(<= ?qw37 ?gx2)) (max ?qw65&:(>= ?qw65 ?gx1)) (com ?gy)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw34&:(<= ?qw34 ?gx1)) (max ?qw64&:(>= ?qw64 ?gx1)) (com ?gy) (layer ?lay)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw38&:(<= ?qw38 ?gx2)) (max ?qw66&:(>= ?qw66 ?gx2)) (com ?gy) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw41&:(<= ?qw41 ?gy)) (max ?qw67&:(>= ?qw67 ?gy)) (com ?gx1) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw42&:(<= ?qw42 ?gy)) (max ?qw68&:(>= ?qw68 ?gy)) (com ?gx2) (layer ?lay)))
  =>
  (retract ?f2)
  (assert (horizontal (com ?gy) (min ?min) (max ?gx2) (compo ?cpo) (commo ?cmo) (layer ?lay) (min-net ?nn1) (max-net ?nn)))
  (assert (horizontal (com ?gy) (min ?gx1) (max ?max) (compo ?cpo) (commo ?cmo) (layer ?lay) (min-net ?nn) (max-net ?nn2)))
  (modify ?f4 (min ?gx2) (max ?gx1) (net-name ?nn) (pin-name ?pn))
)

(defrule p563
  (next-segment ?nn row ?gy nil ?nmin ?nmax)
  ?f2 <- (ff (net-name ?nn) (grid-x ?gx1&:(and (>= ?gx1 ?nmin) (<= ?gx1 ?nmax))) (grid-y ?gy) (grid-layer ?garb1) (pin-name ?pn))
  ?f3 <- (ff (net-name ?nn) (grid-x ?gx2&:(and (> ?gx2 ?gx1) (<= ?gx2 ?nmax))) (grid-y ?gy))
  (not (ff (net-name ?nn) (grid-x ?qz38&:(and (> ?qz38 ?gx1) (< ?qz38 ?gx2))) (grid-y ?gy)))
  ?f4 <- (horizontal (net-name nil) (min ?min&:(<= ?min ?gx1)) (max ?max&:(and (>= ?max ?gx2) (> ?max ?min))) (com ?gy) (layer ?lay) (compo ?cpo) (commo ?cmo) (min-net ?nn1) (max-net ?nn2))
  (horizontal-layer ?lay ?)
  (dominant-layer)
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qw34&:(<= ?qw34 ?gx1)) (max ?qw62&:(>= ?qw62 ?gx2)) (com ?gy)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw35&:(<= ?qw35 ?gx1)) (max ?qw65&:(>= ?qw65 ?gx1)) (com ?gy) (layer ?lay)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw37&:(<= ?qw37 ?gx2)) (max ?qw63&:(>= ?qw63 ?gx2)) (com ?gy) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw64&:(>= ?qw64 ?gy)) (com ?gx1) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw40&:(<= ?qw40 ?gy)) (max ?qw66&:(>= ?qw66 ?gy)) (com ?gx2) (layer ?lay)))
  =>
  (retract ?f2 ?f3)
  (assert (horizontal (com ?gy) (min ?min) (max ?gx1) (compo ?cpo) (commo ?cmo) (layer ?lay) (min-net ?nn1) (max-net ?nn)))
  (assert (horizontal (com ?gy) (min ?gx2) (max ?max) (compo ?cpo) (commo ?cmo) (layer ?lay) (min-net ?nn) (max-net ?nn2)))
  (modify ?f4 (min ?gx1) (max ?gx2) (net-name ?nn) (pin-name ?pn))
)

(defrule p564
  (next-segment ?nn col ?gx nil ?nmin ?nmax)
  ?f2 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy1&:(and (>= ?gy1 ?nmin) (<= ?gy1 ?nmax))) (grid-layer ?garb1) (pin-name ?pn))
  ?f3 <- (ff (net-name ?nn) (grid-y ?gy2&:(and (> ?gy2 ?gy1) (<= ?gy2 ?nmax))) (grid-x ?gx))
  (not (ff (net-name ?nn) (grid-y ?qz38&:(and (> ?qz38 ?gy1) (< ?qz38 ?gy2))) (grid-x ?gx)))
  ?f4 <- (vertical (net-name nil) (min ?min&:(<= ?min ?gy1)) (max ?max&:(and (>= ?max ?gy2) (> ?max ?min))) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo) (min-net ?nn1) (max-net ?nn2))
  (not (vertical (net-name nil) (min ?qw74&:(<= ?qw74 ?gy1)) (max ?qw65&:(>= ?qw65 ?gy2)) (com ?gx) (layer ~?lay)))
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qw75&:(<= ?qw75 ?gy1)) (max ?qw62&:(>= ?qw62 ?gy2)) (com ?gx)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw31&:(<= ?qw31 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy1) (layer ?lay)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw45&:(>= ?qw45 ?gx)) (com ?gy2) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw76&:(<= ?qw76 ?gy1)) (max ?qw63&:(>= ?qw63 ?gy1)) (com ?gx) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw77&:(<= ?qw77 ?gy2)) (max ?qw64&:(>= ?qw64 ?gy2)) (com ?gx) (layer ?lay)))
  =>
  (retract ?f2 ?f3)
  (assert (vertical (com ?gx) (min ?min) (max ?gy1) (compo ?cpo) (commo ?cmo) (layer ?lay) (min-net ?nn1) (max-net ?nn)))
  (assert (vertical (com ?gx) (min ?gy2) (max ?max) (compo ?cpo) (commo ?cmo) (layer ?lay) (min-net ?nn) (max-net ?nn2)))
  (modify ?f4 (min ?gy1) (max ?gy2) (net-name ?nn) (pin-name ?pn))
)

(defrule p565
  (next-segment ?nn col ?gx nil ?nmin ?nmax)
  ?f2 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy1&:(and (>= ?gy1 ?nmin) (<= ?gy1 ?nmax))) (grid-layer ?garb1) (pin-name ?pn))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy2&:(and (> ?gy2 ?gy1) (<= ?gy2 ?nmax))))
  (not (ff (net-name ?nn) (grid-y ?qz38&:(and (> ?qz38 ?gy1) (< ?qz38 ?gy2))) (grid-x ?gx)))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qw31&:(<= ?qw31 ?gx)) (max ?qw45&:(>= ?qw45 ?gx)) (com ?qz39&:(and (> ?qz39 ?gy1) (< ?qz39 ?gy2)))))
  ?f4 <- (vertical (net-name nil) (min ?min&:(<= ?min ?gy1)) (max ?max&:(and (>= ?max ?gy2) (> ?max ?min))) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo) (min-net ?nn1) (max-net ?nn2))
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qw74&:(<= ?qw74 ?gy1)) (max ?qw63&:(>= ?qw63 ?gy2)) (com ?gx)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw32&:(<= ?qw32 ?gx)) (max ?qw46&:(>= ?qw46 ?gx)) (com ?gy1) (layer ?lay)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw33&:(<= ?qw33 ?gx)) (max ?qw47&:(>= ?qw47 ?gx)) (com ?gy2) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw75&:(<= ?qw75 ?gy1)) (max ?qw62&:(>= ?qw62 ?gy1)) (com ?gx) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw76&:(<= ?qw76 ?gy2)) (max ?qw64&:(>= ?qw64 ?gy2)) (com ?gx) (layer ?lay)))
  =>
  (retract ?f2)
  (assert (vertical (com ?gx) (min ?min) (max ?gy1) (compo ?cpo) (commo ?cmo) (layer ?lay) (min-net ?nn1) (max-net ?nn)))
  (assert (vertical (com ?gx) (min ?gy2) (max ?max) (compo ?cpo) (commo ?cmo) (layer ?lay) (min-net ?nn) (max-net ?nn2)))
  (modify ?f4 (min ?gy1) (max ?gy2) (net-name ?nn) (pin-name ?pn))
)

(defrule p566
  (next-segment ?nn col ?gx nil ?nmin ?nmax)
  ?f2 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy1&:(and (>= ?gy1 ?nmin) (<= ?gy1 ?nmax))) (grid-layer ?garb1) (pin-name ?pn))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy2&:(and (< ?gy2 ?gy1) (>= ?gy2 ?nmin))))
  (not (ff (net-name ?nn) (grid-y ?qz38&:(and (> ?qz38 ?gy2) (< ?qz38 ?gy1))) (grid-x ?gx)))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qw31&:(<= ?qw31 ?gx)) (max ?qw45&:(>= ?qw45 ?gx)) (com ?qz39&:(and (> ?qz39 ?gy2) (< ?qz39 ?gy1)))))
  ?f4 <- (vertical (net-name nil) (min ?min&:(<= ?min ?gy2)) (max ?max&:(and (>= ?max ?gy1) (> ?max ?min))) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo) (min-net ?nn1) (max-net ?nn2))
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qw74&:(<= ?qw74 ?gy2)) (max ?qw63&:(>= ?qw63 ?gy1)) (com ?gx)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw32&:(<= ?qw32 ?gx)) (max ?qw46&:(>= ?qw46 ?gx)) (com ?gy1) (layer ?lay)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw33&:(<= ?qw33 ?gx)) (max ?qw47&:(>= ?qw47 ?gx)) (com ?gy2) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw75&:(<= ?qw75 ?gy1)) (max ?qw62&:(>= ?qw62 ?gy1)) (com ?gx) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw76&:(<= ?qw76 ?gy2)) (max ?qw64&:(>= ?qw64 ?gy2)) (com ?gx) (layer ?lay)))
  =>
  (retract ?f2)
  (assert (vertical (com ?gx) (min ?min) (max ?gy2) (compo ?cpo) (commo ?cmo) (layer ?lay) (min-net ?nn1) (max-net ?nn)))
  (assert (vertical (com ?gx) (min ?gy1) (max ?max) (compo ?cpo) (commo ?cmo) (layer ?lay) (min-net ?nn) (max-net ?nn2)))
  (modify ?f4 (min ?gy2) (max ?gy1) (net-name ?nn) (pin-name ?pn))
)

(defrule p567
  (next-segment ?nn col ?gx nil ?nmin ?nmax)
  ?f2 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy1&:(and (>= ?gy1 ?nmin) (<= ?gy1 ?nmax))) (grid-layer ?garb1) (pin-name ?pn))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy2&:(and (> ?gy2 ?gy1) (<= ?gy2 ?nmax))))
  (not (ff (net-name ?nn) (grid-y ?qz38&:(and (> ?qz38 ?gy1) (< ?qz38 ?gy2))) (grid-x ?gx)))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qw31&:(<= ?qw31 ?gx)) (max ?qw45&:(>= ?qw45 ?gx)) (com ?qz39&:(and (> ?qz39 ?gy1) (< ?qz39 ?gy2)))))
  ?f4 <- (vertical (net-name nil) (min ?min&:(<= ?min ?gy1)) (max ?max&:(and (>= ?max ?gy2) (> ?max ?min))) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo) (min-net ?nn1) (max-net ?nn2))
  (vertical-layer ?lay ?)
  (dominant-layer)
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qw74&:(<= ?qw74 ?gy1)) (max ?qw63&:(>= ?qw63 ?gy2)) (com ?gx)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw32&:(<= ?qw32 ?gx)) (max ?qw46&:(>= ?qw46 ?gx)) (com ?gy1) (layer ?lay)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw33&:(<= ?qw33 ?gx)) (max ?qw47&:(>= ?qw47 ?gx)) (com ?gy2) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw75&:(<= ?qw75 ?gy1)) (max ?qw62&:(>= ?qw62 ?gy1)) (com ?gx) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw76&:(<= ?qw76 ?gy2)) (max ?qw64&:(>= ?qw64 ?gy2)) (com ?gx) (layer ?lay)))
  =>
  (retract ?f2)
  (assert (vertical (com ?gx) (min ?min) (max ?gy1) (compo ?cpo) (commo ?cmo) (layer ?lay) (min-net ?nn1) (max-net ?nn)))
  (assert (vertical (com ?gx) (min ?gy2) (max ?max) (compo ?cpo) (commo ?cmo) (layer ?lay) (min-net ?nn) (max-net ?nn2)))
  (modify ?f4 (min ?gy1) (max ?gy2) (net-name ?nn) (pin-name ?pn))
)

(defrule p568
  (next-segment ?nn col ?gx nil ?nmin ?nmax)
  ?f2 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy1&:(and (>= ?gy1 ?nmin) (<= ?gy1 ?nmax))) (grid-layer ?garb1) (pin-name ?pn))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?qw31&:(<= ?qw31 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy2&:(and (< ?gy2 ?gy1) (>= ?gy2 ?nmin))))
  (not (ff (net-name ?nn) (grid-y ?qz38&:(and (> ?qz38 ?gy2) (< ?qz38 ?gy1))) (grid-x ?gx)))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw45&:(>= ?qw45 ?gx)) (com ?qz39&:(and (> ?qz39 ?gy2) (< ?qz39 ?gy1)))))
  ?f4 <- (vertical (net-name nil) (min ?min&:(<= ?min ?gy2)) (max ?max&:(and (>= ?max ?gy1) (> ?max ?min))) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo) (min-net ?nn1) (max-net ?nn2))
  (vertical-layer ?lay ?)
  (dominant-layer)
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qw74&:(<= ?qw74 ?gy2)) (max ?qw63&:(>= ?qw63 ?gy1)) (com ?gx)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw32&:(<= ?qw32 ?gx)) (max ?qw46&:(>= ?qw46 ?gx)) (com ?gy1) (layer ?lay)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw33&:(<= ?qw33 ?gx)) (max ?qw47&:(>= ?qw47 ?gx)) (com ?gy2) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw75&:(<= ?qw75 ?gy1)) (max ?qw62&:(>= ?qw62 ?gy1)) (com ?gx) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw76&:(<= ?qw76 ?gy2)) (max ?qw64&:(>= ?qw64 ?gy2)) (com ?gx) (layer ?lay)))
  =>
  (retract ?f2)
  (assert (vertical (com ?gx) (min ?min) (max ?gy2) (compo ?cpo) (commo ?cmo) (layer ?lay) (min-net ?nn1) (max-net ?nn)))
  (assert (vertical (com ?gx) (min ?gy1) (max ?max) (compo ?cpo) (commo ?cmo) (layer ?lay) (min-net ?nn) (max-net ?nn2)))
  (modify ?f4 (min ?gy2) (max ?gy1) (net-name ?nn) (pin-name ?pn))
)

(defrule p569
  (next-segment ?nn col ?gx nil ?nmin ?nmax)
  ?f2 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy1&:(and (>= ?gy1 ?nmin) (<= ?gy1 ?nmax))) (grid-layer ?garb1) (pin-name ?pn))
  ?f3 <- (ff (net-name ?nn) (grid-y ?gy2&:(and (> ?gy2 ?gy1) (<= ?gy2 ?nmax))) (grid-x ?gx))
  (not (ff (net-name ?nn) (grid-y ?qz38&:(and (> ?qz38 ?gy1) (< ?qz38 ?gy2))) (grid-x ?gx)))
  ?f4 <- (vertical (net-name nil) (min ?min&:(<= ?min ?gy1)) (max ?max&:(and (>= ?max ?gy2) (> ?max ?min))) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo) (min-net ?nn1) (max-net ?nn2))
  (vertical-layer ?lay ?)
  (dominant-layer)
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qw74&:(<= ?qw74 ?gy1)) (max ?qw62&:(>= ?qw62 ?gy2)) (com ?gx)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy1) (layer ?lay)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw31&:(<= ?qw31 ?gx)) (max ?qw45&:(>= ?qw45 ?gx)) (com ?gy2) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw75&:(<= ?qw75 ?gy1)) (max ?qw63&:(>= ?qw63 ?gy1)) (com ?gx) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw76&:(<= ?qw76 ?gy2)) (max ?qw64&:(>= ?qw64 ?gy2)) (com ?gx) (layer ?lay)))
  =>
  (retract ?f2 ?f3)
  (assert (vertical (com ?gx) (min ?min) (max ?gy1) (compo ?cpo) (commo ?cmo) (layer ?lay) (min-net ?nn1) (max-net ?nn)))
  (assert (vertical (com ?gx) (min ?gy2) (max ?max) (compo ?cpo) (commo ?cmo) (layer ?lay) (min-net ?nn) (max-net ?nn2)))
  (modify ?f4 (min ?gy1) (max ?gy2) (net-name ?nn) (pin-name ?pn))
)

(defrule p570
  (next-segment ?nn row ?gy nil ?nmin ?nmax)
  (horizontal (status nil) (net-name ?nn&~nil) (min ?garb1) (max ?max&:(and (>= ?max ?nmin) (<= ?max ?nmax))) (com ?gy) (layer ?garb2) (compo ?garb3) (commo ?garb4) (pin-name ?pn))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qw74&:(<= ?qw74 ?max)) (max ?qw86&:(> ?qw86 ?max)) (com ?gy)))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?min&:(and (> ?min ?max) (<= ?min ?nmax))) (com ?gy))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qz20&:(< ?qz20 ?min)) (max ?qw6&:(>= ?qw6 ?min)) (com ?gy)))
  ?f4 <- (horizontal (net-name nil) (min ?max) (max ?min&:(> ?min ?max)) (com ?gy) (layer ?garb5) (compo ?garb6) (commo ?garb7))
  =>
  (modify ?f4 (net-name ?nn) (pin-name ?pn) (max-net nil) (min-net nil))
)

(defrule p571
  (next-segment ?nn col ?gx nil ?nmin ?nmax)
  (vertical (status nil) (net-name ?nn&~nil) (min ?garb1) (max ?max&:(and (>= ?max ?nmin) (<= ?max ?nmax))) (com ?gx) (layer ?garb2) (compo ?garb3) (commo ?garb4) (pin-name ?pn))
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qw75&:(<= ?qw75 ?max)) (max ?qw86&:(> ?qw86 ?max)) (com ?gx)))
  (vertical (status nil) (net-name ?nn&~nil) (min ?min&:(and (> ?min ?max) (<= ?min ?nmax))) (com ?gx))
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qz20&:(< ?qz20 ?min)) (max ?qw6&:(>= ?qw6 ?min)) (com ?gx)))
  ?f4 <- (vertical (net-name nil) (min ?max) (max ?min&:(> ?min ?max)) (com ?gx) (layer ?garb5) (compo ?garb6) (commo ?garb7))
  =>
  (modify ?f4 (net-name ?nn) (pin-name ?pn) (max-net nil) (min-net nil))
)

(defrule p572
  ?next <- (next-segment ?nn row ?gy ? ?t5 ?t6)
  (ff (net-name ?nn) (grid-y ?gy) (pin-name ?pn) (came-from north))
  (net (net-name ?nn) (net-no-of-pins ?np) (no-of-top-pins ?np) (no-of-bottom-pins ?garb1))
  (not (ff (net-name ?nn) (grid-y ?qz20&:(< ?qz20 ?gy))))
  ?ff <- (ff (net-name ?nn) (grid-y ?gy2&:(> ?gy2 ?gy)) (pin-name ?pn2) (grid-x ?gx) (grid-layer ?lay) (came-from north))
  ?v1 <- (vertical (net-name nil) (min ?min&:(<= ?min ?gy)) (max ?qz65&:(and (> ?qz65 ?min) (>= ?qz65 ?gy2))) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo) (min-net ?mn))
  (not (vertical (status nil) (net-name ~nil&~?nn) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw62&:(>= ?qw62 ?gy)) (com ?gx) (layer ?lay)))
  (not (horizontal (status nil) (net-name ~nil&~?nn) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?qz47&:(and (< ?qz47 ?gy2) (>= ?qz47 ?gy))) (layer ?lay)))
  =>
  (assert (vertical (net-name nil) (max ?gy) (min ?min) (layer ?lay) (com ?gx) (commo ?cmo) (compo ?cpo) (min-net ?mn) (max-net ?nn)))
  (modify ?v1 (min ?gy2) (min-net ?nn))
  (assert (vertical (net-name ?nn) (max ?gy2) (min ?gy) (layer ?lay) (com ?gx) (commo ?cmo) (compo ?cpo) (pin-name ?pn2)))
  (modify ?ff (grid-y ?gy) (can-chng-layer nil))
  (retract ?next)
  (assert (next-segment ?nn row ?gy useless ?t5 ?t6))
)

(defrule p573
  ?next <- (next-segment ?nn row ?gy ? ? ?)
  (ff (came-from south) (net-name ?nn) (grid-y ?gy) (pin-name ?pn))
  (net (net-name ?nn) (net-no-of-pins ?np) (no-of-top-pins ?garb1) (no-of-bottom-pins ?np))
  (not (ff (net-name ?nn) (grid-y ?qw86&:(> ?qw86 ?gy))))
  ?ff <- (ff (net-name ?nn) (grid-y ?gy2&:(< ?gy2 ?gy)) (pin-name ?pn2) (grid-x ?gx) (grid-layer ?lay) (came-from south))
  ?v1 <- (vertical (net-name nil) (max ?max&:(>= ?max ?gy)) (min ?qz37&:(and (< ?qz37 ?max) (<= ?qz37 ?gy2))) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo) (max-net ?mn))
  (not (vertical (status nil) (net-name ~nil&~?nn) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw62&:(>= ?qw62 ?gy)) (com ?gx) (layer ?lay)))
  (not (horizontal (status nil) (net-name ~nil&~?nn) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?qz30&:(and (> ?qz30 ?gy2) (<= ?qz30 ?gy))) (layer ?lay)))
  =>
  (assert (vertical (net-name nil) (max ?max) (min ?gy) (layer ?lay) (com ?gx) (commo ?cmo) (compo ?cpo) (min-net ?nn) (max-net ?mn)))
  (modify ?v1 (max ?gy2) (max-net ?nn))
  (assert (vertical (net-name ?nn) (max ?gy) (min ?gy2) (layer ?lay) (com ?gx) (commo ?cmo) (compo ?cpo) (pin-name ?pn2)))
  (modify ?ff (grid-y ?gy) (can-chng-layer nil))
  (modify ?v1 (compo useless))
)

(defrule p574
  ?f1 <- (next-segment ?nn row ?gy ?gg1 ?nmin ?nmax)
  ?f2 <- (ff (net-name ?nn) (grid-y ?gy2&:(> ?gy2 ?gy)) (grid-x ?gx&:(and (>= ?gx ?nmin) (<= ?gx ?nmax))) (grid-layer ?lay) (pin-name ?pn))
  ?f3 <- (vertical (net-name nil) (min ?min&:(<= ?min ?gy)) (max ?qz65&:(and (> ?qz65 ?min) (>= ?qz65 ?gy2))) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo) (min-net ?mn))
  (vertical-s (net-name ?nn) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw62&:(>= ?qw62 ?gy2)) (com ?gx))
  (not (vertical (status nil) (net-name ~nil&~?nn) (com ?gx)))
  (not (horizontal (status nil) (net-name ~nil&~?nn) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy) (layer ?lay)))
  =>
  (assert (vertical (net-name nil) (max ?gy) (min ?min) (layer ?lay) (com ?gx) (commo ?cmo) (compo ?cpo) (min-net ?mn) (max-net ?nn)))
  (modify ?f3 (min ?gy2) (min-net ?nn))
  (assert (vertical (net-name ?nn) (max ?gy2) (min ?gy) (layer ?lay) (com ?gx) (commo ?cmo) (compo ?cpo) (pin-name ?pn)))
  (modify ?f2 (grid-y ?gy) (can-chng-layer nil))
  (retract ?f1)
  (assert (next-segment ?nn row ?gy ?gg1 ?nmin ?nmax))
)

(defrule p575
  ?f1 <- (next-segment ?nn row ?gy ?gg1 ?nmin ?nmax)
  ?f2 <- (vertical (net-name ?nn&~nil) (min ?gy2&:(> ?gy2 ?gy)) (com ?gx&:(and (>= ?gx ?nmin) (<= ?gx ?nmax))) (layer ?lay) (pin-name ?pn))
  ?f3 <- (vertical (net-name nil) (min ?qz37&:(and (< ?qz37 ?gy2) (<= ?qz37 ?gy))) (max ?gy2) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo))
  (vertical-s (net-name ?nn) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw62&:(>= ?qw62 ?gy2)) (com ?gx))
  (not (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy2)))
  (not (vertical (net-name ?nn&~nil) (min ?qw40&:(<= ?qw40 ?gy)) (max ?qw63&:(>= ?qw63 ?gy2)) (com ?gx)))
  (not (ff (net-name ~?nn) (grid-x ?gx)))
  (not (vertical (status nil) (net-name ~nil&~?nn) (com ?gx)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy) (layer ?lay)))
  =>
  (modify ?f3 (max ?gy) (max-net ?nn))
  (modify ?f2 (min ?gy))
  (assert (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?pn) (came-from north)))
  (retract ?f1)
  (assert (next-segment ?nn row ?gy ?gg1 ?nmin ?nmax))
)



(defrule p576
  ?f1 <- (next-segment ?nn row ?gy ?gg1 ?nmin ?nmax)
  ?f2 <- (ff (net-name ?nn) (grid-y ?gy2&:(< ?gy2 ?gy)) (grid-x ?gx&:(and (>= ?gx ?nmin) (<= ?gx ?nmax))) (grid-layer ?lay) (pin-name ?pn))
  ?f3 <- (vertical (net-name nil) (min ?min&:(<= ?min ?gy2)) (max ?qz65&:(and (> ?qz65 ?min) (>= ?qz65 ?gy))) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo) (min-net ?mn))
  (vertical-s (net-name ?nn) (min ?qw74&:(<= ?qw74 ?gy2)) (max ?qw62&:(>= ?qw62 ?gy)) (com ?gx))
  (not (vertical (status nil) (net-name ~nil&~?nn) (com ?gx)))
  (not (horizontal (status nil) (net-name ~nil&~?nn) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy) (layer ?lay)))
  =>
  (assert (vertical (net-name nil) (max ?gy2) (min ?min) (layer ?lay) (com ?gx) (commo ?cmo) (compo ?cpo) (min-net ?mn) (max-net ?nn)))
  (modify ?f3 (min ?gy) (min-net ?nn))
  (assert (vertical (net-name ?nn) (max ?gy) (min ?gy2) (layer ?lay) (com ?gx) (commo ?cmo) (compo ?cpo) (pin-name ?pn)))
  (modify ?f2 (grid-y ?gy) (can-chng-layer nil))
  (retract ?f1)
  (assert (next-segment ?nn row ?gy ?gg1 ?nmin ?nmax))
)

(defrule p577
  ?f1 <- (next-segment ?nn row ?gy ?gg1 ?nmin ?nmax)
  ?f2 <- (vertical (net-name ?nn&~nil) (min ?gy2&:(< ?gy2 ?gy)) (com ?gx&:(and (>= ?gx ?nmin) (<= ?gx ?nmax))) (layer ?lay) (pin-name ?pn))
  ?f3 <- (vertical (net-name nil) (min ?gy2) (max ?qz65&:(and (> ?qz65 ?gy2) (>= ?qz65 ?gy))) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo))
  (vertical-s (net-name ?nn) (min ?qw74&:(<= ?qw74 ?gy2)) (max ?qw62&:(>= ?qw62 ?gy)) (com ?gx))
  (not (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy2)))
  (not (vertical (net-name ?nn&~nil) (min ?qw75&:(<= ?qw75 ?gy2)) (max ?qw63&:(>= ?qw63 ?gy)) (com ?gx)))
  (not (ff (net-name ~?nn) (grid-x ?gx)))
  (not (vertical (status nil) (net-name ~nil&~?nn) (com ?gx)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy) (layer ?lay)))
  =>
  (modify ?f3 (min ?gy) (min-net ?nn))
  (modify ?f2 (max ?gy))
  (assert (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?pn) (came-from south)))
  (retract ?f1)
  (assert (next-segment ?nn row ?gy ?gg1 ?nmin ?nmax))
)

(defrule p578
  (next-segment ?nn ? ? ? ? ?)
  ?f2 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?garb1) (pin-name ?garb2))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy) (layer ?garb3) (compo ?garb4) (commo ?garb5))
  (vertical (status nil) (net-name ?nn&~nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw62&:(>= ?qw62 ?gy)) (com ?gx) (layer ?garb6) (compo ?garb7) (commo ?garb8))
  =>
  (retract ?f2)
)

(defrule p579
  (next-segment ?nn ? ? ? ? ?)
  ?f2 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?garb1) (pin-name ?garb2))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?qz20&:(< ?qz20 ?gx)) (max ?gx) (com ?gy))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?gx) (max ?qw86&:(> ?qw86 ?gx)) (com ?gy))
  =>
  (retract ?f2)
)

(defrule p580
  (change-priority)
  ?n <- (next-segment ?t2 ?t3 ?t4 useless ?t6 ?t7)
  =>
  (retract ?n)
  (assert (next-segment ?t2 ?t3 ?t4 nil ?t6 ?t7))
)

(defrule p581
  (change-priority)
  ?n <- (next-segment ?nn col ?x useless ?min ?max)
  (vertical-s (net-name ?nn) (com ?x) (sum 2) (difference 0))
  (last-row ?lr)
  =>
  (retract ?n)
  (assert (next-segment ?nn col ?x nil 1 ?lr))
)

(defrule p582
  (change-priority)
  ?n <- (next-segment ?nn row ?y useless ?min ?max)
  (horizontal-s (net-name ?nn) (com ?x) (sum 2) (difference 0))
  (last-col ?lc)
  =>
  (retract ?n)
  (assert (next-segment ?nn row ?y nil 1 ?lc))
)

(defrule p583
  ?n <- (next-segment ?nn ?rc ?xy nil ? ?)
  (not (next-segment ? ? ? ~nil ? ?))
  =>
  (retract ?n)
  (assert (move-ff ?nn ?rc ?xy))
)

(defrule p584
  ?c <- (change-priority)
  =>
  (retract ?c)
  (assert (end-of-specialized-move-ff))
)

(defrule p585
  (move-ff ?nn row ?xy)
  (horizontal (status nil) (net-name ?nn&~nil) (min ?min) (max ?max) (com ?xy) (layer ?lay) (compo ?cpo) (commo ?garb1))
  ?ff <- (ff (came-from south) (net-name ?nn1&~?nn) (grid-x ?gx&:(and (>= ?gx ?min) (<= ?gx ?max))) (grid-y ?xy) (grid-layer ?flay&~?lay))
  ?v1 <- (vertical (status nil) (net-name ?nn1) (min ?qz20&:(< ?qz20 ?xy)) (max ?xy) (com ?gx) (layer ?flay))
  (not (horizontal (net-name nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?xy) (layer ?flay)))
  ?v2 <- (vertical (net-name nil) (min ?xy) (max ?garb4&:(> ?garb4 ?xy)) (com ?gx) (layer ?flay) (compo ?garb2) (commo ?garb3))
  =>
  (modify ?v2 (min ?cpo) (min-net ?nn1))
  (modify ?v1 (max ?cpo))
  (modify ?ff (grid-y ?cpo) (can-chng-layer nil))
)

(defrule p586
  (move-ff ?nn row ?xy)
  (horizontal (status nil) (net-name ?nn&~nil) (min ?min) (max ?max) (com ?xy) (layer ?lay) (compo ?garb1) (commo ?cmo))
  ?ff <- (ff (came-from north) (net-name ?nn1&~?nn) (grid-x ?gx&:(and (>= ?gx ?min) (<= ?gx ?max))) (grid-y ?xy) (grid-layer ?flay&~?lay))
  ?v1 <- (vertical (status nil) (net-name ?nn1) (min ?xy) (max ?qw86&:(> ?qw86 ?xy)) (com ?gx) (layer ?flay))
  (not (horizontal (net-name nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?xy) (layer ?flay)))
  ?v2 <- (vertical (net-name nil) (min ?vmin) (max ?xy&:(> ?xy ?vmin)) (com ?gx) (layer ?flay) (compo ?garb2) (commo ?garb3))
  =>
  (modify ?v2 (max ?cmo) (max-net ?nn1))
  (modify ?v1 (min ?cmo))
  (modify ?ff (grid-y ?cmo) (can-chng-layer nil))
)

(defrule p587
  (move-ff ?nn ? ?)
  (horizontal (net-name ?nn) (min ?min) (max ?max))
  ?h <- (horizontal-s (net-name ?nn) (min ?min) (max ?max))
  =>
  (retract ?h)
)

(defrule p588
  (move-ff ?nn ? ?)
  (vertical (net-name ?nn) (min ?min) (max ?max))
  ?v <- (vertical-s (net-name ?nn) (min ?min) (max ?max))
  =>
  (retract ?v)
)

(defrule p589
  (move-ff ?nn ? ?)
  (horizontal (net-name ?nn) (min ?min) (max ?max) (com ?com))
  (ff (net-name ?nn) (grid-x ?gx&:(and (>= ?gx ?min) (<= ?gx ?max))) (grid-y ?gy&:(< ?gy ?com)))
  ?v <- (vertical-s (net-name ?nn) (min ?vmin&:(<= ?vmin ?gy)) (max ?vmax&:(>= ?vmax ?com)) (com ?gx) (id ?id))
  (vertical (net-name nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw62&:(>= ?qw62 ?com)) (com ?gx))
  (not (vertical-s (net-name ~?nn) (com ?gx)))
  =>
  (retract ?v)
  (assert (next-segment ?nn col ?gx nil ?vmin ?vmax))
)

(defrule p590
  (move-ff ?nn ? ?)
  (horizontal (net-name ?nn) (min ?min) (max ?max) (com ?com))
  (ff (net-name ?nn) (grid-x ?gx&:(and (>= ?gx ?min) (<= ?gx ?max))) (grid-y ?gy&:(> ?gy ?com)))
  ?v <- (vertical-s (net-name ?nn) (min ?vmin&:(<= ?vmin ?com)) (max ?vmax&:(>= ?vmax ?gy)) (com ?gx) (id ?id))
  (vertical (net-name nil) (min ?qw74&:(<= ?qw74 ?com)) (max ?qw62&:(>= ?qw62 ?gy)) (com ?gx))
  (not (vertical-s (net-name ~?nn) (com ?gx)))
  =>
  (retract ?v)
  (assert (next-segment ?nn col ?gx nil ?vmin ?vmax))
)
