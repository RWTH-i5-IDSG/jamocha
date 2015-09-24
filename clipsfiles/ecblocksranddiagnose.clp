(defglobal ?*exc* = trivial)
(defglobal ?*nmc* = ecblocksrand)

(deftemplate stage (slot value))
(deftemplate line (slot p1 (type INTEGER)) (slot p2 (type INTEGER)))
(deftemplate edge (slot p1 (type INTEGER)) (slot p2 (type INTEGER)) (slot joined (type BOOLEAN)) (slot label) (slot plotted))
(deftemplate junction (slot p1 (type INTEGER)) (slot p2 (type INTEGER)) (slot p3 (type INTEGER)) (slot base_point (type INTEGER)) (slot jtype))

(set-compiler ?*exc*)
(defrule begin
	?f1 <- (stage (value start))
	=>
	(assert (line (p1 0122) (p2 0107)))
	(assert (line (p1 0107) (p2 2207)))
	(assert (line (p1 2207) (p2 3204)))
	(assert (line (p1 3204) (p2 6404)))
	(assert (line (p1 2216) (p2 2207)))
	(assert (line (p1 3213) (p2 3204)))
	(assert (line (p1 2216) (p2 3213)))
	(assert (line (p1 0107) (p2 2601)))
	(assert (line (p1 2601) (p2 7401)))
	(assert (line (p1 6404) (p2 7401)))
	(assert (line (p1 3213) (p2 6413)))
	(assert (line (p1 6413) (p2 6404)))
	(assert (line (p1 7416) (p2 7401)))
	(assert (line (p1 5216) (p2 6413)))
	(assert (line (p1 2216) (p2 5216)))
	(assert (line (p1 0122) (p2 5222)))
	(assert (line (p1 5222) (p2 7416)))
	(assert (line (p1 5222) (p2 5216)))
	(modify ?f1 (value duplicateStage)))

;;; **********************************************************************
;;; reverse_edges: If the duplicate flag is set, and there is still a line
;;;   in WM, delete the line and add two edges. One edge runs from p1 to
;;;   p2 and the other runs from p2 to p1. We then plot the edge.
;;; **********************************************************************

(set-compiler ?*exc*)
(defrule reverse_edges
	(stage (value duplicateStage))
	?f2 <- (line (p1 ?p1) (p2 ?p2))
	=>
	(assert (edge (p1 ?p1) (p2 ?p2) (joined false)))
    (assert (edge (p1 ?p2) (p2 ?p1) (joined false)))
	(retract ?f2))

;;; **************************************************************************
;;; done_reversing: If the duplicating flag is set, and there are no more
;;;   lines, then remove the duplicating flag and set the make junctions flag.
;;; **************************************************************************

(set-compiler ?*exc*)
(defrule done_reversing
	(declare (salience -10))
	?f1 <- (stage (value duplicateStage))
	(not (line))
	=>
	(modify ?f1 (value detect_junctions)))

;;; *****************************************************************************
;;; make-3_junction: If three edges meet at a point and none of them have already
;;;   been joined in a junction, then make the corresponding jtype of junction and
;;;   label the edges joined.  This production calls make-3_junction to determine
;;;   what jtype of junction it is based on the angles inscribed by the
;;;   intersecting edges.
;;; *****************************************************************************

(set-compiler ?*nmc*)
(defrule make-3_junction
	(declare (salience 10))
	(stage (value detect_junctions))
	?f2 <- (edge (p1 ?base_point) (p2 ?p1) (joined false))
	?f3 <- (edge (p1 ?base_point) (p2 ?p2&~?p1) (joined false))
	?f4 <- (edge (p1 ?base_point) (p2 ?p3&~?p1&~?p2) (joined false))
	=>
	(make-3-junction ?base_point ?p1 ?p2 ?p3)
	(modify ?f2 (joined true))
	(modify ?f3 (joined true))
	(modify ?f4 (joined true)))

;;; ******************************************************
;;; make_L: If two, and only two, edges meet that have not
;;;   already been joined, then the junction is an "L".
;;; ******************************************************

(set-compiler ?*exc*)
(defrule make_L
	(stage (value detect_junctions))
	?f2 <- (edge (p1 ?base_point) (p2 ?p2) (joined false))
	?f3 <- (edge (p1 ?base_point) (p2 ?p3&~?p2) (joined false))
	(not (edge (p1 ?base_point) (p2 ~?p2&~?p3)))
	=>
	(assert (junction (jtype L)
               		  (base_point ?base_point)
		              (p1 ?p2)
		              (p2 ?p3)))
	(modify ?f2 (joined true))
	(modify ?f3 (joined true)))

;;; ******************************************************************
;;; done_detecting: If the detect junctions flag is set, and there are
;;;   no more un_joined edges, set the find_initial_boundary flag.
;;; ******************************************************************

(set-compiler ?*exc*)
(defrule done_detecting
	(declare (salience -10))
	?f1 <- (stage (value detect_junctions))
	(not (edge (joined false)))
	=>
	(modify ?f1 (value find_initial_boundary)))

;;; ****************************************************
;;; initial_boundary_junction_L: If the initial boundary
;;;   junction is an L, then we know it's labelling
;;; ****************************************************

(set-compiler ?*exc*)
(defrule initial_boundary_junction_L
	?f1 <- (stage (value find_initial_boundary))
    (junction (jtype L)
              (base_point ?base_point)
              (p1 ?p1)
              (p2 ?p2))
	?f3 <- (edge (p1 ?base_point) (p2 ?p1))
	?f4 <- (edge (p1 ?base_point) (p2 ?p2))
    (not (junction (base_point ?bp&:(> ?bp ?base_point))))
	=>
    (modify ?f3 (label B))
	(modify ?f4 (label B))
	(modify ?f1 (value find_second_boundary)))

;;; ***************************************************
;;; initial_boundary_junction_arrow: Ditto for an arrow
;;; ***************************************************

(set-compiler ?*exc*)
(defrule initial_boundary_junction_arrow
	?f1 <- (stage (value find_initial_boundary))
	(junction (jtype arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
	?f3 <- (edge (p1 ?bp) (p2 ?p1))
	?f4 <- (edge (p1 ?bp) (p2 ?p2))
	?f5 <- (edge (p1 ?bp) (p2 ?p3))
	(not (junction (base_point ?base_point&:(> ?base_point ?bp))))
	=>
    (modify ?f3 (label B))
	(modify ?f4 (label +))
	(modify ?f5 (label B))
	(modify ?f1 (value find_second_boundary)))

;;; ***********************************************************************
;;; second_boundary_junction_L: If we have already found the first boundary
;;;   point, then find the second boundary point, and label it.
;;; ***********************************************************************

(set-compiler ?*exc*)
(defrule second_boundary_junction_L
	?f1 <- (stage (value find_second_boundary))
    (junction (jtype L) (base_point ?base_point) (p1 ?p1) (p2 ?p2))
	?f3 <- (edge (p1 ?base_point) (p2 ?p1))
	?f4 <- (edge (p1 ?base_point) (p2 ?p2))
    (not (junction (base_point ?bp&:(< ?bp ?base_point))))
	=>
    (modify ?f3 (label B))
    (modify ?f4 (label B))
    (modify ?f1 (value labeling)))

;;; ******************************
;;; second_boundary_junction_arrow
;;; ******************************

(set-compiler ?*exc*)
(defrule second_boundary_junction_arrow
	?f1 <- (stage (value find_second_boundary))
	(junction (jtype arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
	?f3 <- (edge (p1 ?bp) (p2 ?p1))
	?f4 <- (edge (p1 ?bp) (p2 ?p2))
	?f5 <- (edge (p1 ?bp) (p2 ?p3))
	(not (junction (base_point ?base_point&:(< ?base_point ?bp))))
	=>
    (modify ?f3 (label B))
	(modify ?f4 (label +))
	(modify ?f5 (label B))
	(modify ?f1 (value labeling)))

;;; **********************************************************************
;;; match_edge: If we have an edge whose label we already know definitely,
;;;   then label the corresponding edge in the other direction
;;; **********************************************************************

(set-compiler ?*exc*)
(defrule match_edge
	(stage (value labeling))
	?f2 <- (edge (p1 ?p1) (p2 ?p2) (label ?label& + | - | B))
	?f3 <- (edge (p1 ?p2) (p2 ?p1) (label nil))
	=>
	(modify ?f2 (plotted t))
	(modify ?f3 (label ?label) (plotted t)))

;;; The following productions propogate the possible labellings of the edges
;;; based on the labellings of edges incident on adjacent junctions.  Since
;;; from the initial boundary productions, we have determined the labellings of
;;; of atleast two junctions, this propogation will label all of the junctions
;;; with the possible labellings.  The search space is pruned due to filtering,
;;; i.e. - only label a junction in the ways physically possible based on the
;;; labellings of adjacent junctions.

;;; *******
;;; label_L
;;; *******

(set-compiler ?*exc*)
(defrule label_L
	(stage (value labeling))
	(junction (jtype L) (base_point ?p1))
	(edge (p1 ?p1) (p2 ?p2) (label + | -))
	?f4 <- (edge (p1 ?p1) (p2 ~?p2) (label nil))
	=>
	(modify ?f4 (label B)))

;;; ***********
;;; label_tee_A
;;; ***********

(set-compiler ?*exc*)
(defrule label_tee_A
	(declare (salience 5))
	(stage (value labeling))
	(junction (jtype tee) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
	?f3 <- (edge (p1 ?bp) (p2 ?p1) (label nil))
	?f4 <- (edge (p1 ?bp) (p2 ?p3))
	=>
    (modify ?f3 (label B))
	(modify ?f4 (label B)))

;;; ***********
;;; label_tee_B
;;; ***********

(set-compiler ?*exc*)
(defrule label_tee_B
	(stage (value labeling))
	(junction (jtype tee) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
	?f3 <- (edge (p1 ?bp) (p2 ?p1))
	?f4 <- (edge (p1 ?bp) (p2 ?p3) (label nil))
	=>
    (modify ?f3 (label B))
	(modify ?f4 (label B)))

;;; ************
;;; label_fork-1
;;; ************

(set-compiler ?*exc*)
(defrules
(label_fork-1
	(stage (value labeling))
	(junction (jtype fork) (base_point ?bp))
	(edge (p1 ?bp) (p2 ?p1) (label +))
	?f4 <- (edge (p1 ?bp) (p2 ?p2&~?p1) (label nil))
	?f5 <- (edge (p1 ?bp) (p2 ~?p2&~?p1))
	=>
	(modify ?f4 (label +))
	(modify ?f5 (label +)))

;;; ************
;;; label_fork-2
;;; ************

; (set-compiler ?*exc*)
(label_fork-2
	(stage (value labeling))
	(junction (jtype fork) (base_point ?bp))
	(edge (p1 ?bp) (p2 ?p1) (label B))
	(edge (p1 ?bp) (p2 ?p2&~?p1) (label -))
	?f5 <- (edge (p1 ?bp) (p2 ~?p2&~?p1) (label nil))
	=>
	(modify ?f5 (label B)))

;;; ************
;;; label_fork-3
;;; ************

; (set-compiler ?*exc*)
(label_fork-3
	(stage (value labeling))
	(junction (jtype fork) (base_point ?bp))
	(edge (p1 ?bp) (p2 ?p1) (label B))
	(edge (p1 ?bp) (p2 ?p2&~?p1) (label B))
	?f5 <- (edge (p1 ?bp) (p2 ~?p2&~?p1) (label nil))
	=>
	(modify ?f5 (label -)))

;;; ************
;;; label_fork-4
;;; ************

; (set-compiler ?*exc*)
(label_fork-4
	(stage (value labeling))
	(junction (jtype fork) (base_point ?bp))
	(edge (p1 ?bp) (p2 ?p1) (label -))
	(edge (p1 ?bp) (p2 ?p2&~?p1) (label -))
	?f5 <- (edge (p1 ?bp) (p2 ~?p2&~?p1) (label nil))
	=>
	(modify ?f5 (label -)))
)

;;; **************
;;; label_arrow-1A
;;; **************

(set-compiler ?*exc*)
(defrule label_arrow-1A
	(declare (salience 5))
	(stage (value labeling))
	(junction (jtype arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
	(edge (p1 ?bp) (p2 ?p1) (label ?label& B | - ))
	?f4 <- (edge (p1 ?bp) (p2 ?p2) (label nil))
	?f5 <- (edge (p1 ?bp) (p2 ?p3))
	=>
	(modify ?f4 (label +))
	(modify ?f5 (label ?label)))

;;; **************
;;; label_arrow-1B
;;; **************

(set-compiler ?*exc*)
(defrule label_arrow-1B
	(stage (value labeling))
	(junction (jtype arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
	(edge (p1 ?bp) (p2 ?p1) (label ?label& B | - ))
	?f4 <- (edge (p1 ?bp) (p2 ?p2))
	?f5 <- (edge (p1 ?bp) (p2 ?p3) (label nil))
	=>
	(modify ?f4 (label +))
	(modify ?f5 (label ?label)))


;;; **************
;;; label_arrow-2A
;;; **************

(set-compiler ?*exc*)
(defrule label_arrow-2A
	(declare (salience 5))
	(stage (value labeling))
	(junction (jtype arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
	(edge (p1 ?bp) (p2 ?p3) (label ?label& B | - ))
	?f4 <- (edge (p1 ?bp) (p2 ?p2) (label nil))
	?f5 <- (edge (p1 ?bp) (p2 ?p1))
	=>
	(modify ?f4 (label +))
	(modify ?f5 (label ?label)))

;;; **************
;;; label_arrow-2B
;;; **************

(set-compiler ?*exc*)
(defrule label_arrow-2B
	(stage (value labeling))
	(junction (jtype arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
	(edge (p1 ?bp) (p2 ?p3) (label ?label& B | - ))
	?f4 <- (edge (p1 ?bp) (p2 ?p2))
	?f5 <- (edge (p1 ?bp) (p2 ?p1) (label nil))
	=>
	(modify ?f4 (label +))
	(modify ?f5 (label ?label)))


;;; **************
;;; label_arrow-3A
;;; **************

(set-compiler ?*exc*)
(defrule label_arrow-3A
	(declare (salience 5))
	(stage (value labeling))
	(junction (jtype arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
	(edge (p1 ?bp) (p2 ?p1) (label +))
	?f4 <- (edge (p1 ?bp) (p2 ?p2) (label nil))
	?f5 <- (edge (p1 ?bp) (p2 ?p3))
	=>
	(modify ?f4 (label -))
	(modify ?f5 (label +)))

;;; **************
;;; label_arrow-3B
;;; **************

(set-compiler ?*exc*)
(defrule label_arrow-3B
	(stage (value labeling))
	(junction (jtype arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
	(edge (p1 ?bp) (p2 ?p1) (label +))
	?f4 <- (edge (p1 ?bp) (p2 ?p2))
	?f5 <- (edge (p1 ?bp) (p2 ?p3) (label nil))
	=>
	(modify ?f4 (label -))
	(modify ?f5 (label +)))

;;; **************
;;; label_arrow-4A
;;; **************

(set-compiler ?*exc*)
(defrule label_arrow-4A
	(declare (salience 5))
	(stage (value labeling))
	(junction (jtype arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
	(edge (p1 ?bp) (p2 ?p3) (label +))
	?f4 <- (edge (p1 ?bp) (p2 ?p2) (label nil))
	?f5 <- (edge (p1 ?bp) (p2 ?p1))
	=>
	(modify ?f4 (label -))
	(modify ?f5 (label +)))

;;; **************
;;; label_arrow-4B
;;; **************

(set-compiler ?*exc*)
(defrule label_arrow-4B
	(stage (value labeling))
	(junction (jtype arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
	(edge (p1 ?bp) (p2 ?p3) (label +))
	?f4 <- (edge (p1 ?bp) (p2 ?p2))
	?f5 <- (edge (p1 ?bp) (p2 ?p1) (label nil))
	=>
	(modify ?f4 (label -))
	(modify ?f5 (label +)))

;;; **************
;;; label_arrow-5A
;;; **************

(set-compiler ?*exc*)
(defrule label_arrow-5A
	(declare (salience 5))
	(stage (value labeling))
	(junction (jtype arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
	(edge (p1 ?bp) (p2 ?p2) (label -))
	?f4 <- (edge (p1 ?bp) (p2 ?p1))
	?f5 <- (edge (p1 ?bp) (p2 ?p3) (label nil))
	=>
	(modify ?f4 (label +))
	(modify ?f5 (label +)))

;;; **************
;;; label_arrow-5B
;;; **************

(set-compiler ?*exc*)
(defrule label_arrow-5B
	(stage (value labeling))
	(junction (jtype arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
	(edge (p1 ?bp) (p2 ?p2) (label -))
	?f4 <- (edge (p1 ?bp) (p2 ?p1) (label nil))
	?f5 <- (edge (p1 ?bp) (p2 ?p3))
	=>
	(modify ?f4 (label +))
	(modify ?f5 (label +)))

;;; *******************************************************************************
;;; done_labeling: The conflict resolution mechanism will only execute a production
;;;   if no productions that are more complicated are satisfied.  This production
;;;   is simple, so all of the above dictionary productions will fire before this
;;;   change of state production
;;; *******************************************************************************

(set-compiler ?*exc*)
(defrule done_labeling
	(declare (salience -10))
	?f1 <- (stage (value labeling))
	=>
	(modify ?f1 (value plot_remaining_edges)))

;;; **************************************************
;;; plot_remaining: At this point, some labellings may
;;;   have not been plotted, so plot them
;;; **************************************************

(set-compiler ?*exc*)
(defrule plot_remaining
	(stage (value plot_remaining_edges))
	?f2 <- (edge (plotted nil) (label ?label&~nil) (p1 ?p1) (p2 ?p2))
	=>
	(modify ?f2 (plotted t)))

;;; ********************************************************************************
;;; plot_boundaries: If we have been un able to label an edge, assume that it is a
;;;   boundary. This is a total Kludge, but what the hell. (if we assume only
;;;   valid drawings will be given for labeling, this assumption generally is true!)
;;; ********************************************************************************

(set-compiler ?*exc*)
(defrule plot_boundaries
	(stage (value plot_remaining_edges))
	?f2 <- (edge (plotted nil) (label nil) (p1 ?p1) (p2 ?p2))
	=>
	(modify ?f2 (plotted t)))

;;; ****************************************************************************
;;; done_plotting: If there is no more work to do, then we are done and flag it.
;;; ****************************************************************************

(set-compiler ?*exc*)
(defrule done_plotting
	(declare (salience -10))
	?f1 <- (stage (value plot_remaining_edges))
	(not (edge (plotted nil)))
	=>
	(modify ?f1 (value done)))


(export-gv "now.gv" label_fork-1 label_fork-2 label_fork-3 label_fork-4)
(load-facts clipsfiles/waltz/waltz25.fct)
(run)
(exit)
