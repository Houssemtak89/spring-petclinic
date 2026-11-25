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
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;

/**
 * Test class for {@link VisitController}
 *
 * @author Colin But
 * @author Wick Dynex
 */
@WebMvcTest(VisitController.class)
@DisabledInNativeImage
@DisabledInAotMode
class VisitControllerTests {

	private static final int TEST_OWNER_ID = 1;

	private static final int TEST_PET_ID = 1;

	private static final String DUPLICATE_VISIT_DATE_MESSAGE = "This pet already has a visit on this date.";

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OwnerRepository owners;

	private Owner owner;

	private Pet pet;

	@BeforeEach
	void init() {
		this.owner = new Owner();
		this.pet = new Pet();
		this.owner.addPet(this.pet);
		this.pet.setId(TEST_PET_ID);
		given(this.owners.findById(TEST_OWNER_ID)).willReturn(Optional.of(this.owner));
	}

	private static Visit visitOn(LocalDate date) {
		Visit visit = new Visit();
		visit.setDate(date);
		visit.setDescription("Existing visit");
		return visit;
	}

	@Test
	void initNewVisitForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void processNewVisitFormSuccess() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("name", "George")
				.param("date", LocalDate.now().plusDays(1).toString())
				.param("description", "Visit Description"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void processNewVisitFormHasErrors() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID).param("name",
					"George"))
			.andExpect(model().attributeHasErrors("visit"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void processNewVisitFormHasErrorsWhenVisitDateIsNotInFuture() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("name", "George")
				.param("date", LocalDate.now().toString())
				.param("description", "Visit Description"))
			.andExpect(model().attributeHasFieldErrors("visit", "date"))
			.andExpect(model().attributeHasFieldErrorCode("visit", "date", "typeMismatch.visitDate"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void processNewVisitFormHasErrorsWhenDescriptionIsTooLong() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("name", "George")
				.param("date", LocalDate.now().plusDays(1).toString())
				.param("description", "a".repeat(256)))
			.andExpect(model().attributeHasFieldErrors("visit", "description"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	@DisplayName("F1: a visit on a date where the pet already has a visit is refused on the date field and not saved")
	void f1_refusesSecondVisitOfSamePetOnSameDate() throws Exception {
		LocalDate date = LocalDate.now().plusDays(3);
		this.pet.addVisit(visitOn(date));

		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", date.toString())
				.param("description", "Second visit"))
			.andExpect(view().name("pets/createOrUpdateVisitForm"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrorCode("visit", "date", "duplicate.visitDate"));

		then(this.owners).should(never()).save(any(Owner.class));
	}

	@ParameterizedTest(name = "F2: duplicate.visitDate resolves to the agreed English sentence for locale \"{0}\"")
	@ValueSource(strings = { "", "de", "es", "fa", "hi", "ja", "ko", "pt", "ru", "tr" })
	void f2_duplicateVisitDateMessageHasAgreedTextInEveryBundle(String language) {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("messages/messages");
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setFallbackToSystemLocale(false);

		String message = messageSource.getMessage("duplicate.visitDate", null, null, Locale.forLanguageTag(language));

		assertThat(message).isEqualTo(DUPLICATE_VISIT_DATE_MESSAGE);
	}

	@ParameterizedTest(
			name = "F2: the bundle for locale \"{0}\" defines duplicate.visitDate itself, not through the default bundle")
	@ValueSource(strings = { "de", "es", "fa", "hi", "ja", "ko", "pt", "ru", "tr" })
	void f2_localeBundleDefinesDuplicateVisitDateItself(String language) throws IOException {
		Properties bundle = new Properties();
		try (Reader reader = new InputStreamReader(
				new ClassPathResource("messages/messages_" + language + ".properties").getInputStream(),
				StandardCharsets.UTF_8)) {
			bundle.load(reader);
		}

		assertThat(bundle.getProperty("duplicate.visitDate")).isEqualTo(DUPLICATE_VISIT_DATE_MESSAGE);
	}

	@Test
	@DisplayName("F2: a refused duplicate visit renders the agreed sentence on the form")
	void f2_refusedDuplicateVisitRendersAgreedMessage() throws Exception {
		LocalDate date = LocalDate.now().plusDays(3);
		this.pet.addVisit(visitOn(date));

		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", date.toString())
				.param("description", "Second visit"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"))
			.andExpect(content().string(containsString(DUPLICATE_VISIT_DATE_MESSAGE)));
	}

	@Test
	@DisplayName("F2: a refused duplicate visit renders the same English sentence when the user chose another language")
	void f2_refusedDuplicateVisitRendersEnglishMessageInOtherLanguage() throws Exception {
		LocalDate date = LocalDate.now().plusDays(3);
		this.pet.addVisit(visitOn(date));

		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID).param("lang", "de")
				.param("date", date.toString())
				.param("description", "Second visit"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"))
			// the German "Add Visit" button label proves the page really rendered in
			// German
			.andExpect(content().string(containsString("Besuch hinzuf")))
			.andExpect(content().string(containsString(DUPLICATE_VISIT_DATE_MESSAGE)));
	}

	@Test
	@DisplayName("F3:a visit for the same pet on a different date is accepted")
	void f3_acceptsVisitOfSamePetOnDifferentDate() throws Exception {
		LocalDate date = LocalDate.now().plusDays(3);
		this.pet.addVisit(visitOn(date.plusDays(1)));

		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", date.toString())
				.param("description", "Another visit"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));

		then(this.owners).should().save(this.owner);
	}

	@Test
	@DisplayName("F4: a visit on a date where only another pet of the owner has a visit is accepted")
	void f4_acceptsVisitWhenOnlyAnotherPetHasVisitOnDate() throws Exception {
		LocalDate date = LocalDate.now().plusDays(3);
		Pet otherPet = new Pet();
		this.owner.addPet(otherPet);
		otherPet.setId(TEST_PET_ID + 1);
		otherPet.addVisit(visitOn(date));

		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", date.toString())
				.param("description", "Visit for the first pet"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));

		then(this.owners).should().save(this.owner);
	}

	@Test
	@DisplayName("F6: a past date the pet already has a visit on shows only the future-date error on the date field")
	void f6_pastDuplicateDateShowsOnlyFutureDateError() throws Exception {
		LocalDate pastDate = LocalDate.now().minusDays(3);
		this.pet.addVisit(visitOn(pastDate));

		MvcResult result = mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", pastDate.toString())
				.param("description", "Visit on a past taken date"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"))
			.andReturn();

		BindingResult bindingResult = (BindingResult) result.getModelAndView()
			.getModel()
			.get(BindingResult.MODEL_KEY_PREFIX + "visit");
		assertThat(bindingResult.getFieldErrors("date")).extracting(FieldError::getCode)
			.containsExactly("typeMismatch.visitDate");

		then(this.owners).should(never()).save(any(Owner.class));
	}

}
