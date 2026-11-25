/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Pet#hasVisitOn(LocalDate, Visit)}.
 */
class PetTests {

	private static final LocalDate DATE = LocalDate.of(2030, 6, 15);

	private static Visit visitOn(LocalDate date) {
		Visit visit = new Visit();
		visit.setDate(date);
		return visit;
	}

	@Test
	@DisplayName("F1: another visit of the pet on the date is found")
	void f1_findsAnotherVisitOnSameDate() {
		Pet pet = new Pet();
		pet.addVisit(visitOn(DATE));
		Visit beingCreated = visitOn(DATE);
		pet.addVisit(beingCreated);

		assertThat(pet.hasVisitOn(DATE, beingCreated)).isTrue();
	}

	@Test
	@DisplayName("F1: another visit of the pet on the date is found when no visit is ignored")
	void f1_findsVisitOnSameDateWhenIgnoredIsNull() {
		Pet pet = new Pet();
		pet.addVisit(visitOn(DATE));

		assertThat(pet.hasVisitOn(DATE, null)).isTrue();
	}

	@Test
	@DisplayName("F1: the ignored visit alone does not count as a visit on the date")
	void f1_ignoredVisitAloneDoesNotCount() {
		Pet pet = new Pet();
		Visit beingCreated = visitOn(DATE);
		pet.addVisit(beingCreated);

		assertThat(pet.hasVisitOn(DATE, beingCreated)).isFalse();
	}

	@Test
	@DisplayName("F1: a pet with no visits has no visit on the date")
	void f1_noVisitsMeansNoVisitOnDate() {
		Pet pet = new Pet();

		assertThat(pet.hasVisitOn(DATE, null)).isFalse();
	}

	@Test
	@DisplayName("F3: a visit of the pet on a different date does not count")
	void f3_visitOnDifferentDateDoesNotCount() {
		Pet pet = new Pet();
		pet.addVisit(visitOn(DATE.plusDays(1)));
		Visit beingCreated = visitOn(DATE);
		pet.addVisit(beingCreated);

		assertThat(pet.hasVisitOn(DATE, beingCreated)).isFalse();
	}

}
