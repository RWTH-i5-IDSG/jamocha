/*
 * Copyright 2002-2014 The Jamocha Team
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.languages.common;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.languages.common.ConditionalElement.AndFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.ExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.InitialFactConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NegatedExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NotFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.OrFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.SharedConditionalElementWrapper;
import org.jamocha.languages.common.ConditionalElement.TestConditionalElement;

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 *
 */
public class RuleConditionProcessor {

	public static ConditionalElement moveNots(ConditionalElement ce) {
		NotFunctionConditionalElementSeep seep = new NotFunctionConditionalElementSeep();
		ce.accept(seep);
		return seep.getCe();
	}

	public static void combineNested(ConditionalElement ce) {
		final CombineNested cn = new CombineNested();
		ce.accept(cn);
	}

	public static ConditionalElement expandOrs(ConditionalElement ce) {
		final ExpandOrs eo = new ExpandOrs();
		ce.accept(eo);
		if (eo.ces.size() == 1) {
			return eo.ces.get(0);
		} else {
			return new OrFunctionConditionalElement(eo.ces);
		}
	}

	private static class ExpandOrs implements ConditionalElementsVisitor {

		@Getter
		private List<ConditionalElement> ces = new ArrayList<ConditionalElement>();

		@Override
		public void visit(AndFunctionConditionalElement ce) {
			List<List<ConditionalElement>> tmpList = ce.children.stream().map((el) -> {
				ExpandOrs eo = new ExpandOrs();
				el.accept(eo);
				return eo.getCes();
			}).collect(Collectors.toList());
			int nez = (int) tmpList.stream().filter(el -> el.size() != 1).count();
			switch (nez) {
			case 0:
				ces.add(ce);
				break;
			default:
				final SharedConditionalElementWrapper sce =
						new SharedConditionalElementWrapper(new AndFunctionConditionalElement(
								new ArrayList<ConditionalElement>(ce.children.size() - nez)));
				List<ConditionalElement> children = new ArrayList<ConditionalElement>(nez + 1);
				children.add(sce);
				ces.add(new AndFunctionConditionalElement(children));
				tmpList.forEach(el -> {
					if (el.size() == 1) {
						sce.getChildren().add(el.get(0));
					} else {
						List<ConditionalElement> newCes = new ArrayList<>(el.size() * ces.size());
						el.forEach(subel -> ces.forEach(l -> {
							List<ConditionalElement> andChildren = new ArrayList<>();
							newCes.add(new AndFunctionConditionalElement(andChildren));
							andChildren.addAll(l.getChildren());
							ConditionalElement tmp = subel;
							if (nez != 1) {
								tmp = new SharedConditionalElementWrapper(subel);
								andChildren.add(tmp);
							} else {
								StripAnds sa = new StripAnds();
								subel.accept(sa);
								andChildren.addAll(sa.getCes());
							}
						}));
						ces = newCes;
					}
				});
				if (sce.getChildren().size() == 1)
					sce.replaceConditionalElement(sce.getChildren().get(0));
			}
		}

		@Override
		public void visit(ExistentialConditionalElement ce) {
			ces.add(ce);
		}

		@Override
		public void visit(InitialFactConditionalElement ce) {
			ces.add(ce);
		}

		@Override
		public void visit(NegatedExistentialConditionalElement ce) {
			ces.add(ce);
		}

		@Override
		public void visit(NotFunctionConditionalElement ce) {
			ces.add(ce);
		}

		@Override
		public void visit(OrFunctionConditionalElement ce) {
			ce.children.forEach(el -> {
				ExpandOrs eo = new ExpandOrs();
				el.accept(eo);
				ces.addAll(eo.getCes());
			});
		}

		@Override
		public void visit(TestConditionalElement ce) {
			ces.add(ce);
		}
	}

	private static class CombineNested implements ConditionalElementsVisitor {

		@Override
		public void visit(AndFunctionConditionalElement ce) {
			ce.getChildren().forEach((child) -> {
				child.accept(this);
			});
			List<ConditionalElement> oldChildrenList =
					new ArrayList<ConditionalElement>(ce.getChildren());
			List<ConditionalElement> childrenList = ce.getChildren();
			childrenList.clear();
			for (ConditionalElement conditionalElement : oldChildrenList) {
				final StripAnds sa = new StripAnds();
				conditionalElement.accept(sa);
				childrenList.addAll(sa.getCes());
			}
		}

		@Override
		public void visit(OrFunctionConditionalElement ce) {
			ce.getChildren().forEach((child) -> {
				child.accept(this);
			});
			List<ConditionalElement> oldChildrenList =
					new ArrayList<ConditionalElement>(ce.getChildren());
			List<ConditionalElement> childrenList = ce.getChildren();
			childrenList.clear();
			for (ConditionalElement conditionalElement : oldChildrenList) {
				final StripOrs so = new StripOrs();
				conditionalElement.accept(so);
				childrenList.addAll(so.getCes());
			}
		}

		@Override
		public void visit(ExistentialConditionalElement ce) {
		}

		@Override
		public void visit(InitialFactConditionalElement ce) {
		}

		@Override
		public void visit(NegatedExistentialConditionalElement ce) {
		}

		@Override
		public void visit(NotFunctionConditionalElement ce) {
		}

		@Override
		public void visit(TestConditionalElement ce) {
		}

	}

	@RequiredArgsConstructor
	private static class NotFunctionConditionalElementSeep implements ConditionalElementsVisitor {

		final private boolean negated;
		@Getter
		private ConditionalElement ce = null;

		public NotFunctionConditionalElementSeep() {
			negated = false;
		}

		private void processChilds(ConditionalElement ce, boolean nextNegated) {
			ce.getChildren().replaceAll(
					(ConditionalElement x) -> {
						NotFunctionConditionalElementSeep seep =
								new NotFunctionConditionalElementSeep(nextNegated);
						x.accept(seep);
						return seep.ce;
					});
		}

		private void visitLeaf(ConditionalElement ce) {
			if (negated) {
				List<ConditionalElement> list = new ArrayList<>();
				list.add(ce);
				this.ce = new NotFunctionConditionalElement(list);
			} else {
				this.ce = ce;
			}
		}

		@Override
		public void visit(final AndFunctionConditionalElement ce) {
			if (negated) {
				this.ce = new OrFunctionConditionalElement(ce.getChildren());
			} else {
				this.ce = ce;
			}
			processChilds(this.ce, negated);
		}

		@Override
		public void visit(final ExistentialConditionalElement ce) {
			throw new Error(
					"Found existential conditional element inside not function conditional element.");
		}

		@Override
		public void visit(final InitialFactConditionalElement ce) {
			visitLeaf(ce);
		}

		@Override
		public void visit(final NegatedExistentialConditionalElement ce) {
			throw new Error(
					"Found negated existential conditional element inside not function conditional element.");
		}

		@Override
		public void visit(final NotFunctionConditionalElement ce) {
			if (ce.getChildren().size() > 1) {
				this.ce = new AndFunctionConditionalElement(ce.getChildren());
			} else {
				this.ce = ce.getChildren().get(0);
			}
			NotFunctionConditionalElementSeep seep =
					new NotFunctionConditionalElementSeep(!negated);
			this.ce.accept(seep);
			this.ce = seep.ce;
		}

		@Override
		public void visit(final OrFunctionConditionalElement ce) {
			if (negated) {
				this.ce = new AndFunctionConditionalElement(ce.getChildren());
			} else {
				this.ce = ce;
			}
			processChilds(this.ce, negated);
		}

		@Override
		public void visit(final TestConditionalElement ce) {
			visitLeaf(ce);
		}

	}

	private static class StripAnds implements ConditionalElementsVisitor {

		@Getter
		private List<ConditionalElement> ces;

		private List<ConditionalElement> wrapInList(final ConditionalElement ce) {
			final List<ConditionalElement> ceList = new ArrayList<ConditionalElement>(1);
			ceList.add(ce);
			return ceList;
		}

		@Override
		public void visit(AndFunctionConditionalElement ce) {
			ces = ce.getChildren();
		}

		@Override
		public void visit(ExistentialConditionalElement ce) {
			this.ces = wrapInList(ce);
		}

		@Override
		public void visit(InitialFactConditionalElement ce) {
			this.ces = wrapInList(ce);
		}

		@Override
		public void visit(NegatedExistentialConditionalElement ce) {
			this.ces = wrapInList(ce);
		}

		@Override
		public void visit(NotFunctionConditionalElement ce) {
			this.ces = wrapInList(ce);
		}

		@Override
		public void visit(OrFunctionConditionalElement ce) {
			this.ces = wrapInList(ce);
		}

		@Override
		public void visit(TestConditionalElement ce) {
			this.ces = wrapInList(ce);
		}
		
		@Override
		public void visit(SharedConditionalElementWrapper ce) {
			this.ces = wrapInList(ce);
		}

	}

	private static class StripOrs implements ConditionalElementsVisitor {

		@Getter
		private List<ConditionalElement> ces;

		private List<ConditionalElement> wrapInList(final ConditionalElement ce) {
			final List<ConditionalElement> ceList = new ArrayList<ConditionalElement>(1);
			ceList.add(ce);
			return ceList;
		}

		@Override
		public void visit(AndFunctionConditionalElement ce) {
			this.ces = wrapInList(ce);
		}

		@Override
		public void visit(ExistentialConditionalElement ce) {
			this.ces = wrapInList(ce);
		}

		@Override
		public void visit(InitialFactConditionalElement ce) {
			this.ces = wrapInList(ce);
		}

		@Override
		public void visit(NegatedExistentialConditionalElement ce) {
			this.ces = wrapInList(ce);
		}

		@Override
		public void visit(NotFunctionConditionalElement ce) {
			this.ces = wrapInList(ce);
		}

		@Override
		public void visit(OrFunctionConditionalElement ce) {
			this.ces = ce.getChildren();
		}

		@Override
		public void visit(TestConditionalElement ce) {
			this.ces = wrapInList(ce);
		}

	}
}
