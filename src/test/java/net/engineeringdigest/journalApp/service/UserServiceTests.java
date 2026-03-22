package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class UserServiceTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private  UserService userService;

    @Disabled
    @Test
    public void testAdd(){
//        assertEquals(4,2+2);
//        assertNotNull(userRepository.findByUserName("vipul"));

        User user = userRepository.findByUserName("ram");
        assertTrue(!user.getJournalEntries().isEmpty());
    }



    @Disabled
    @ParameterizedTest
    @CsvSource(
            {
                    "1,1,2",
                    "2,10,12",
                    "3,3,6"
            }
    )
    public void test(int a ,int b,int expected){
        assertEquals(expected,a+b);

    }

//    @Disabled
//    @ParameterizedTest
//    @ValueSource(strings = {
//            "ram",
//            "vipul",
//            "neeraj"
//    }
//    )
//    public void testFindByUserName(String name){
//        assertNotNull(userRepository.findByUserName(name));
//
//    }


    


//    @ParameterizedTest
//    @ArgumentsSource(UserArgumentsProvider.class)   //Used for one custom provider
//    public void testSaveNewUser(User user){
//       assertTrue(userService.saveNewUser(user));
//
//    }








}
