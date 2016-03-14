package com.github.jsonapi.retrofit;

import com.github.jsonapi.ErrorUtils;
import com.github.jsonapi.IOUtils;
import com.github.jsonapi.ResourceConverter;
import com.github.jsonapi.models.User;
import com.github.jsonapi.models.errors.Error;
import com.github.jsonapi.models.errors.ErrorResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import retrofit.Response;
import retrofit.Retrofit;

import java.io.IOException;
import java.util.List;

/**
 * Retrofit plugin tests.
 *
 * @author jbegic
 */
public class RetrofitTest {
	private MockWebServer server;
	private SimpleService service;

	@Before
	public void setup() throws IOException {
		// Setup server
		server = new MockWebServer();
		server.start();

		// Setup retrofit
		ResourceConverter converter = new ResourceConverter(User.class);
		JSONAPIConverterFactory converterFactory = new JSONAPIConverterFactory(converter);

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(server.url("/").toString())
				.addConverterFactory(converterFactory)
				.build();

		service = retrofit.create(SimpleService.class);
	}

	@After
	public void destroy() throws IOException {
		server.shutdown();
	}

	@Test
	public void getResourceTest() throws IOException {
		String userResponse = IOUtils.getResourceAsString("user-liz.json");

		server.enqueue(new MockResponse()
				.setResponseCode(200)
				.setBody(userResponse));

		Response<User> response = service.getExampleResource().execute();

		Assert.assertTrue(response.isSuccess());

		User user = response.body();

		Assert.assertNotNull(user);
		Assert.assertEquals("liz", user.getName());
	}

	@Test
	public void getResourceCollectionTest() throws IOException {
		String usersResponse = IOUtils.getResourceAsString("users.json");

		server.enqueue(new MockResponse()
				.setResponseCode(200)
				.setBody(usersResponse));

		Response<List<User>> response = service.getExampleResourceList().execute();

		Assert.assertTrue(response.isSuccess());

		List<User> users = response.body();
		Assert.assertEquals(2, users.size());
	}

	@Test
	public void testError() throws IOException {
		String errorString = IOUtils.getResourceAsString("errors.json");

		server.enqueue(new MockResponse()
				.setResponseCode(400)
				.setBody(errorString));

		Response<User> response = service.getExampleResource().execute();

		Assert.assertFalse(response.isSuccess());

		ErrorResponse errorResponse = ErrorUtils.parseErrorResponse(response.errorBody());

		Assert.assertNotNull(errorResponse);
		Assert.assertEquals(1, errorResponse.getErrors().size());

		Error error = errorResponse.getErrors().get(0);

		Assert.assertEquals("id", error.getId());
		Assert.assertEquals("status", error.getStatus());
		Assert.assertEquals("code", error.getCode());
		Assert.assertEquals("title", error.getTitle());
		Assert.assertEquals("about", error.getLinks().getAbout());
		Assert.assertEquals("title", error.getTitle());
		Assert.assertEquals("pointer", error.getSource().getPointer());
		Assert.assertEquals("detail", error.getDetail());

		// Shutdown server
		server.shutdown();
	}
}
