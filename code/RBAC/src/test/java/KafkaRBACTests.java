import com.rbac.kafka.DatabaseManager;
import com.rbac.kafka.KafkaController;
import com.rbac.kafka.RBACBootstrapper;
import org.apache.kafka.common.acl.AccessControlEntry;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.acl.AclOperation;
import org.apache.kafka.common.acl.AclPermissionType;
import org.apache.kafka.common.resource.PatternType;
import org.apache.kafka.common.resource.ResourcePattern;
import org.apache.kafka.common.resource.ResourceType;
import org.apache.kafka.common.security.auth.KafkaPrincipal;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class KafkaRBACTests {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private KafkaController mockKafkaController;
    @Mock
    private DatabaseManager mockDatabaseManager;

    @Mock
    private Connection mockConnection;

    @Test
    public void testAddRoleCommand() {
        String[] input = {"addRole", "--roleName", "test-role", "--permissions", "test-topic"};

        try (MockedStatic<RBACBootstrapper> mockedStatic = Mockito.mockStatic(RBACBootstrapper.class)) {
            mockedStatic.when(() -> RBACBootstrapper.handleInput(input, mockDatabaseManager))
                    .thenCallRealMethod();


            RBACBootstrapper.handleInput(input, mockDatabaseManager);

            verify(mockDatabaseManager).addRole("test-role", Collections.singletonList("test-topic"));
        }
    }

    @Test
    public void testAddUserCommand() {
        String[] input = {"addUser", "--userName", "test-user"};

        try (MockedStatic<RBACBootstrapper> mockedStatic = Mockito.mockStatic(RBACBootstrapper.class)) {
            mockedStatic.when(() -> RBACBootstrapper.handleInput(input, mockDatabaseManager))
                    .thenCallRealMethod();


            RBACBootstrapper.handleInput(input, mockDatabaseManager);

            verify(mockDatabaseManager).addUser("test-user");
        }
    }

    @Test
    public void testAddPermissionCommand() {
        String[] input = {"addPermission", "--roleName", "test-role", "--topicName", "test-topic"};

        try (MockedStatic<RBACBootstrapper> mockedStatic = Mockito.mockStatic(RBACBootstrapper.class)) {
            mockedStatic.when(() -> RBACBootstrapper.handleInput(input, mockDatabaseManager))
                    .thenCallRealMethod();

            RBACBootstrapper.handleInput(input, mockDatabaseManager);

            verify(mockDatabaseManager).addPermission("test-role", "test-topic");
        }
    }

    @Test
    public void testAssignRoleCommand() {
        String[] input = {"assignRole", "--userName", "test-user", "--roleName", "test-role"};

        try (MockedStatic<RBACBootstrapper> mockedStatic = Mockito.mockStatic(RBACBootstrapper.class)) {
            mockedStatic.when(() -> RBACBootstrapper.handleInput(input, mockDatabaseManager))
                    .thenCallRealMethod();

            RBACBootstrapper.handleInput(input, mockDatabaseManager);

            verify(mockDatabaseManager).assignRole("test-user", "test-role");
        }
    }

    @Test
    public void testRemoveRoleCommand() {
        String[] input = {"removeRole", "--userName", "test-user", "--roleName", "test-role"};

        try (MockedStatic<RBACBootstrapper> mockedStatic = Mockito.mockStatic(RBACBootstrapper.class)) {
            mockedStatic.when(() -> RBACBootstrapper.handleInput(input, mockDatabaseManager))
                    .thenCallRealMethod();

            RBACBootstrapper.handleInput(input, mockDatabaseManager);

            verify(mockDatabaseManager).removeRole("test-user", "test-role");
        }
    }

    @Test
    public void testDeleteUserCommand() {
        String[] input = {"deleteUser", "--userName", "test-user"};

        try (MockedStatic<RBACBootstrapper> mockedStatic = Mockito.mockStatic(RBACBootstrapper.class)) {
            mockedStatic.when(() -> RBACBootstrapper.handleInput(input, mockDatabaseManager))
                    .thenCallRealMethod();

            RBACBootstrapper.handleInput(input, mockDatabaseManager);

            verify(mockDatabaseManager).deleteUser("test-user");
        }
    }

    @Test
    public void testDeleteRoleCommand() {
        String[] input = {"deleteRole", "--roleName", "test-role"};

        try (MockedStatic<RBACBootstrapper> mockedStatic = Mockito.mockStatic(RBACBootstrapper.class)) {
            mockedStatic.when(() -> RBACBootstrapper.handleInput(input, mockDatabaseManager))
                    .thenCallRealMethod();

            RBACBootstrapper.handleInput(input, mockDatabaseManager);

            verify(mockDatabaseManager).deleteRole("test-role");
        }
    }



    @Test
    public void testRemovePermissionCommand() {
        String[] input = {"removePermission", "--roleName", "test-role", "--topicName", "test-topic"};

        try (MockedStatic<RBACBootstrapper> mockedStatic = Mockito.mockStatic(RBACBootstrapper.class)) {
            mockedStatic.when(() -> RBACBootstrapper.handleInput(input, mockDatabaseManager))
                    .thenCallRealMethod();

            RBACBootstrapper.handleInput(input, mockDatabaseManager);

            verify(mockDatabaseManager).removePermission("test-role", "test-topic");
        }
    }

    @Test
    public void testAddUserACLs() {
        String user = "test-user";

        DatabaseManager databaseManagerSpy = createManagerSpy(user, "");

        databaseManagerSpy.addUser(user);

        verifyNoInteractions(mockKafkaController);
    }

    @Test
    public void testDeleteUserACLs() {
        String user = "test-user";
        String topic1 = "test-topic1";
        String topic2 = "test-topic2";

        Map<String, List<String>> expectedDatabaseChange = Map.of(user, List.of(topic1, topic2));

        DatabaseManager databaseManagerSpy = createManagerSpy(user, "");

        when(databaseManagerSpy.getPermissionsFromStatus(any())).thenReturn(List.of(topic1, topic2));
        when(databaseManagerSpy.getPostPermissionsFromStatus(any())).thenReturn(Collections.emptyList());

        databaseManagerSpy.deleteUser(user);

        verify(mockKafkaController).manageDatabaseChanges(expectedDatabaseChange, false);
    }

    @Test
    public void testAddRoleACLs() {
        String role = "test-role";
        String topic = "test-topic";

        DatabaseManager databaseManagerSpy = createManagerSpy("", role);

        databaseManagerSpy.addRole(role, List.of(topic));

        verifyNoInteractions(mockKafkaController);
    }

    @Test
    public void testDeleteRoleACLs() {
        String user = "test-user";
        String topic = "test-topic";
        String role = "test-role";

        Map<String, List<String>> expectedDatabaseChange = Map.of(user, List.of(topic));

        DatabaseManager databaseManagerSpy = createManagerSpy(user, role);

        when(databaseManagerSpy.getUsersForRole(role)).thenReturn(List.of(user));
        when(databaseManagerSpy.getPermissionsFromStatus(any())).thenReturn(List.of(topic));
        when(databaseManagerSpy.getPostPermissionsFromStatus(any())).thenReturn(Collections.emptyList());

        databaseManagerSpy.deleteRole(role);

        verify(mockKafkaController).manageDatabaseChanges(expectedDatabaseChange, false);
    }

    @Test
    public void testAssignRoleACLs(){
        String user = "test-user";
        String topic = "test-topic";
        String role = "test-role";

        Map<String, List<String>> expectedDatabaseChange = Map.of(user, List.of(topic));

        DatabaseManager databaseManagerSpy = createManagerSpy(user, role);

        when(databaseManagerSpy.getPermissionsFromStatus(any())).thenReturn(Collections.emptyList());
        when(databaseManagerSpy.getPostPermissionsFromStatus(any())).thenReturn(List.of(topic));

        databaseManagerSpy.assignRole(user, role);

        verify(mockKafkaController).manageDatabaseChanges(expectedDatabaseChange, true);
    }

    @Test
    public void testRemoveRoleACLs() {
        String user = "test-user";
        String topic = "test-topic";
        String role = "test-role";

        Map<String, List<String>> expectedDatabaseChange = Map.of(user, List.of(topic));

        DatabaseManager databaseManagerSpy = createManagerSpy(user, role);

        when(databaseManagerSpy.isUserAssignedToRole(user, role)).thenReturn(true);
        when(databaseManagerSpy.getPermissionsFromStatus(any())).thenReturn(List.of(topic));
        when(databaseManagerSpy.getPostPermissionsFromStatus(any())).thenReturn(Collections.emptyList());

        databaseManagerSpy.removeRole(user, role);

        verify(mockKafkaController).manageDatabaseChanges(expectedDatabaseChange, false);
    }

    @Test
    public void testAddPermissionACLs() {
        String user = "test-user";
        String role = "test-role";
        String topic = "test-topic";

        Map<String, List<String>> expectedDatabaseChange = Map.of(user, List.of(topic));

        DatabaseManager databaseManagerSpy = createManagerSpy(user, role);

        when(databaseManagerSpy.permissionExistsForRole(role, topic)).thenReturn(false);
        when(databaseManagerSpy.getUsersForRole(role)).thenReturn(List.of(user));
        when(databaseManagerSpy.getPermissionsFromStatus(any())).thenReturn(Collections.emptyList());
        when(databaseManagerSpy.getPostPermissionsFromStatus(any())).thenReturn(List.of(topic));

        databaseManagerSpy.addPermission(role, topic);

        verify(mockKafkaController).manageDatabaseChanges(expectedDatabaseChange, true);
    }

    @Test
    public void testRemovePermissionACLs() {
        String user1 = "test-user1";
        String user2 = "test-user2";
        String role = "test-role";
        String topic = "test-topic";

        Map<String, List<String>> expectedDatabaseChange = Map.of(user1, List.of(topic), user2, List.of(topic));

        DatabaseManager databaseManagerSpy = createManagerSpy(user1, role);

        when(databaseManagerSpy.permissionExistsForRole(topic, role)).thenReturn(true);
        when(databaseManagerSpy.getUsersForRole(role)).thenReturn(List.of(user1, user2));
        when(databaseManagerSpy.getPermissionsFromStatus(any())).thenReturn(List.of(topic));
        when(databaseManagerSpy.getPostPermissionsFromStatus(any())).thenReturn(Collections.emptyList());

        databaseManagerSpy.removePermission(role, topic);

        verify(mockKafkaController).manageDatabaseChanges(expectedDatabaseChange, false);

    }

    @Test
    public void testAddingACLsToKafka() {
        String user1 = "test-user1";
        String user2 = "test-user2";
        String topic = "test-topic";


        Map<String, List<String>> databaseChanges = Map.of(user1, List.of(topic), user2, List.of(topic));
        boolean constructive = true;

        List<AclBinding> expectedACLs = List.of(
                new AclBinding(new ResourcePattern(ResourceType.TOPIC, topic, PatternType.LITERAL), new AccessControlEntry(new KafkaPrincipal("User", user2).toString(), "*", AclOperation.READ, AclPermissionType.ALLOW)),
                new AclBinding(new ResourcePattern(ResourceType.TOPIC, topic, PatternType.LITERAL), new AccessControlEntry(new KafkaPrincipal("User", user1).toString(), "*", AclOperation.READ, AclPermissionType.ALLOW))
        );

        KafkaController kafkaControllerSpy = spy(new KafkaController());

        doNothing().when(kafkaControllerSpy).initializeKafkaAdmin();
        doNothing().when(kafkaControllerSpy).sendACLs(any());
        doNothing().when(kafkaControllerSpy).closeKafkaAdmin();

        kafkaControllerSpy.manageDatabaseChanges(databaseChanges, constructive);

        verify(kafkaControllerSpy).sendACLs(expectedACLs);
    }

    @Test
    public void testRemovingACLsToKafka() {
        String user = "test-user";
        String topic = "test-topic";

        Map<String, List<String>> databaseChanges = Map.of(user, List.of(topic));
        boolean constructive = false;

        List<AclBinding> expectedACLs = List.of(
                new AclBinding(new ResourcePattern(ResourceType.TOPIC, topic, PatternType.LITERAL), new AccessControlEntry(new KafkaPrincipal("User", user).toString(), "*", AclOperation.READ, AclPermissionType.DENY))
        );

        KafkaController kafkaControllerSpy = spy(new KafkaController());

        doNothing().when(kafkaControllerSpy).initializeKafkaAdmin();
        doNothing().when(kafkaControllerSpy).sendACLs(any());
        doNothing().when(kafkaControllerSpy).closeKafkaAdmin();

        kafkaControllerSpy.manageDatabaseChanges(databaseChanges, constructive);

        verify(kafkaControllerSpy).sendACLs(expectedACLs);
    }


    private DatabaseManager createManagerSpy(String user, String role) {
        DatabaseManager databaseManagerSpy = spy(new DatabaseManager(mockKafkaController));

        try {
            when(databaseManagerSpy.userExists(user)).thenReturn(true);
            when(databaseManagerSpy.roleExists(role)).thenReturn(true);
            when(databaseManagerSpy.getConnection()).thenReturn(mockConnection);
            doNothing().when(databaseManagerSpy).performDatabaseOperation(any());

            when(databaseManagerSpy.userStatus(user)).thenReturn(new HashMap<>());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return databaseManagerSpy;
    }


}
