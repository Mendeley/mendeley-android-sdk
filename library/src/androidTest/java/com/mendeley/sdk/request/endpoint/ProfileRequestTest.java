package com.mendeley.sdk.request.endpoint;

import android.net.Uri;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.JsonReader;

import com.mendeley.sdk.Request;
import com.mendeley.sdk.exceptions.MendeleyException;
import com.mendeley.sdk.model.Education;
import com.mendeley.sdk.model.Employment;
import com.mendeley.sdk.model.Institution;
import com.mendeley.sdk.model.Profile;
import com.mendeley.sdk.request.GetAuthorizedRequest;
import com.mendeley.sdk.request.JsonParser;
import com.mendeley.sdk.request.SignedInTest;
import com.mendeley.sdk.testUtils.AssertUtils;
import com.mendeley.sdk.util.DateUtils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ProfileRequestTest extends SignedInTest {

    @LargeTest
    public void test_getMeProfile_receivesCorrectMeProfile() throws Exception {

        // GIVEN the user profile on the server
        final Profile expectedProfile = createExpectedProfile();
        // setup the profile in the web server
        getTestAccountSetupUtils().setupMeRemoteProfile(expectedProfile);

        // WHEN we query for ME profile
        final Profile actualProfile = getRequestFactory().newGetMyProfileRequest().run().resource;

        // THEN we have the expected profile
        AssertUtils.assertProfile(expectedProfile, actualProfile);
    }

    @LargeTest
    public void test_patchMeProfile_patchedProfileInServer() throws Exception {
        // GIVEN a user profile
        final String firstName = generateRandomString(20);
        final String lastName = generateRandomString(20);
        final String title = generateRandomString(20);
        final String academicStatus = (new String[] {"Other", "Lecturer", "Professor", "Researcher"})[getRandom().nextInt(4)];
        final Institution institution = getRandomInstitution(getExistingInstitutions());

        final Profile patchingProfile = new Profile.Builder()
                .setFirstName(firstName)
                .setLastName(lastName)
                .setTitle(title)
                .setAcademicStatus(academicStatus)
                .setInstitutionDetails(institution)
                .build();


        // WHEN we patch ME profile
        final Profile receivedProfile = getRequestFactory().newPatchMeProfileRequest(patchingProfile).run().resource;

        // THEN the fields that we have patched are the same
        final Profile expectedProfile = new Profile.Builder()
                .setFirstName(firstName)
                .setLastName(lastName)
                .setTitle(title)
                .setAcademicStatus(academicStatus)
                .setInstitutionDetails(institution)
                .build();

        final Profile actualProfile = new Profile.Builder()
                .setFirstName(receivedProfile.firstName)
                .setLastName(receivedProfile.lastName)
                .setTitle(receivedProfile.title)
                .setAcademicStatus(receivedProfile.academicStatus)
                .setInstitutionDetails(receivedProfile.institutionDetails)
                .build();

        AssertUtils.assertProfile(expectedProfile, actualProfile);
    }

    //    /**
//     * As we don't have an API to setup profiles for the test,
//     * the tests rely on profiles we added manually
//     * through the web interface https://www.mendeley.com
//     */
//    @LargeTest
//    public void test_getProfile_receivesCorrectProfile() throws Exception {
//        // GIVEN the user profile on the server
//        final Profile expectedProfile =  new Profile.Builder()
//                .setId("f38dc0c8-df12-32a0-ae70-28ab4f3409cd")
//                .setFirstName("Alfred")
//                .setLastName("Schnittke")
//                .setAcademicStatus("Lecturer")
//                .build();
//
//        getRequestFactory().newPatchMeProfileRequest(expectedProfile).run();
//
//        // WHEN getting the profile
//        final Profile actual = getRequestFactory().newGetProfileRequest(expectedProfile.id).run().resource;
//
//        // THEN we have the expected profile
//        AssertUtils.assertProfile(expectedProfile, actual);
//    }
//
//
//    /**
//     * Cannot run this test as delete /profiles is not available on staging at the moment
//     */
//    public void test_postProfile_createsProfileInServer() throws Exception {
//        if (BuildConfig.FLAVOR.equals("staging")) {
//            // GIVEN a profile
//            final Profile postingProfile = new Profile.Builder().
//                    setFirstName("Alfred").
//                    setLastName("Schnittke").
//                    setEmail("alfred_schnitke@androidtest.com").
//                    setDiscipline(new Discipline("Arts and Humanities")).
//                    setAcademicStatus("Other").
//                    setMarketing(false).
//                    build();
//
//            // WHEN posting it
//            final Profile returnedProfile = getRequestFactory().newPostProfileRequest(postingProfile, "userPassword").run().resource;
//
//            // THEN we receive the same profile back, with id filled
//            AssertUtils.assertNewProfile(postingProfile, returnedProfile);
//            assertNotNull(returnedProfile.id);
//
//            // AND the profile exists in the server
//            AssertUtils.assertNewProfile(postingProfile, getRequestFactory().newGetProfileRequest(returnedProfile.id).run().resource);
//
//            // delete profile from server
//            getRequestFactory().newDeleteProfileRequest(returnedProfile.id).run();
//        }
//    }

    private Profile createExpectedProfile() throws MendeleyException, ParseException {
        // get a random list of institutions to use them as employments and educations
        final List<Institution> institutions = getExistingInstitutions();

        // create a random profile, with random employment and education
        return new Profile.Builder()
                .setFirstName(generateRandomString(20))
                .setLastName(generateRandomString(20))
                .setTitle(generateRandomString(20))
                .setAcademicStatus("Lecturer")
                .setEmployment(Arrays.asList(createEmployment(getRandomInstitution(institutions)), createEmployment(getRandomInstitution(institutions))))
                .setEducation(Arrays.asList(createEducation(getRandomInstitution(institutions)), createEducation(getRandomInstitution(institutions))))
                .build();
    }

    private List<Institution> getExistingInstitutions() throws MendeleyException {

        final Uri url = Uri.parse(Request.MENDELEY_API_BASE_URL).buildUpon()
                .path("institutions")
                .appendQueryParameter("hint", "Complutense")
                .build();

        return new GetAuthorizedRequest<List<Institution>>(url, getAuthManager(), getClientCredentials()) {
            @Override
            protected List<Institution> manageResponse(InputStream is) throws Exception {
                final JsonReader reader = new JsonReader(new InputStreamReader(new BufferedInputStream(is)));
                return JsonParser.institutionsFromJson(reader);
            }

            @Override
            protected void appendHeaders(Map<String, String> headers) {
                headers.put("Content-type", "application/vnd.mendeley-institution.1+json");
            }
        }.run().resource;
    }

    private Institution getRandomInstitution(List<Institution> institutions) {
        return institutions.get(getRandom().nextInt(institutions.size()));
    }

    private Employment createEmployment(Institution institution) throws ParseException {
        return new Employment.Builder()
                .setPosition(generateRandomString(20))
                .setInstitution(institution)
                .setStartDate(DateUtils.parseYearMonthDayDate("2000-10-10"))
                .setEndDate(DateUtils.parseYearMonthDayDate("2002-10-10"))
                .build();
    }

    private Education createEducation(Institution institution) throws ParseException {
        return new Education.Builder()
                .setDegree(generateRandomString(20))
                .setInstitution(institution)
                .setStartDate(DateUtils.parseYearMonthDayDate("2000-10-10"))
                .setEndDate(DateUtils.parseYearMonthDayDate("2002-10-10"))
                .build();
    }


}
