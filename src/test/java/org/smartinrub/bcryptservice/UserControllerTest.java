package org.smartinrub.bcryptservice;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest
public class UserControllerTest {

	public static Long USER_ID = 1L;
	public static String USER_EMAIL = "sergio.rubio@gamesys.co.uk";
	public static String USER_PASSWORD = "Password1";
	public static String WRONG_USER_EMAIL = "wronguser@gmail.com";

	public static User mockUser = new User();

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserRepository userRepository;

	@BeforeClass
	public static void onlyOnce() {
		mockUser.setId(USER_ID);
		mockUser.setEmail(USER_EMAIL);
		mockUser.setPassword(USER_PASSWORD);
	}

	@Test
	public void getUserWhenCorrectIdShouldReturnStatusOkAndUser() throws Exception {
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(mockUser));

		this.mockMvc.perform(get("/" + USER_ID)).andExpect(status().isOk()).andExpect(
				content().json("{\n\"id\": 1,\n\"email\": \"sergio.rubio@gamesys.co.uk\",\n"
						+ "\"password\": \"Password1\"\n}"));
	}

	@Test
	public void saveUserWhenCorrectValuesShouldReturnCreatedStatus() throws Exception {
		when(userRepository.save(mockUser)).thenReturn(mockUser);

		this.mockMvc
				.perform(post("/").contentType(MediaType.APPLICATION_JSON)
						.content("{\n\"email\": \"test@gmail.com\",\n\"password\": \"test\"\n}"))
				.andExpect(status().isCreated());
	}

	@Test
	public void loginWhenValidUserShouldReturnOkStatusAndWelcomeMessage() throws Exception {
		User user = new User();
		user.setEmail(USER_EMAIL);
		user.setPassword(BCrypt.hashpw(USER_PASSWORD, BCrypt.gensalt()));
		when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

		this.mockMvc
				.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
						.content("{\n\"email\": \"sergio.rubio@gamesys.co.uk\",\n\"password\": \"Password1\"\n}"))
				.andExpect(status().isOk()).andExpect(content().string(containsString("Welcome")));
	}

	@Test
	public void loginWhenInValidUserShouldReturnNotFoundAndMessage() throws Exception {
		User user = new User();
		user.setEmail(WRONG_USER_EMAIL);
		user.setPassword(BCrypt.hashpw(USER_PASSWORD, BCrypt.gensalt()));
		when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

		this.mockMvc
				.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(
						"{\n\"email\": \"wronguser@gmail.com\",\n\"password\": \"Password1\"\n}"))
				.andExpect(status().isNotFound()).andExpect(content().string(containsString("User not found")));
	}

	@Test
	public void loginWhenInValidPasswordShouldReturnForbiddenAndMessage() throws Exception {
		User user = new User();
		user.setEmail(USER_EMAIL);
		user.setPassword(BCrypt.hashpw(USER_PASSWORD, BCrypt.gensalt()));
		when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

		this.mockMvc
				.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
						.content("{\n\"email\": \"sergio.rubio@gamesys.co.uk\",\n\"password\": \"wrongpass\"\n}"))
				.andExpect(status().isForbidden()).andExpect(content().string(containsString("Wrong Password!")));
	}

}
