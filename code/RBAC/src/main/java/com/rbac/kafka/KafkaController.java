package com.rbac.kafka;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateAclsResult;
import org.apache.kafka.common.acl.AccessControlEntry;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.acl.AclOperation;
import org.apache.kafka.common.acl.AclPermissionType;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.resource.PatternType;
import org.apache.kafka.common.resource.ResourcePattern;
import org.apache.kafka.common.resource.ResourceType;
import org.apache.kafka.common.security.auth.KafkaPrincipal;

import java.util.*;

import static com.rbac.kafka.RBACUtils.KAFKA_SERVER;

public class KafkaController {

    private AdminClient adminClient;
    public void initializeKafkaAdmin() {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_SERVER);

        properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        properties.put(SaslConfigs.SASL_MECHANISM, "PLAIN");

        String jaasTemplate = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";";
        String jaasCfg = String.format(jaasTemplate, "kafka", "kafka-rbac");
        properties.put(SaslConfigs.SASL_JAAS_CONFIG, jaasCfg);

        this.adminClient = AdminClient.create(properties);

    }

    public void closeKafkaAdmin() {
        if (this.adminClient != null)
            this.adminClient.close();
    }

    public void manageDatabaseChanges(Map<String, List<String>> aclChanges, Boolean constructive) {
        List<AclBinding> aclBindings = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : aclChanges.entrySet()) {
            for (String topic : entry.getValue()) {
                AclBinding aclBinding = translateToACL(entry.getKey(), topic, constructive);
                aclBindings.add(aclBinding);
            }
        }
        sendACLs(aclBindings);
    }

    public void sendACLs(List<AclBinding> aclBindings) {
        if (this.adminClient == null) {
            initializeKafkaAdmin();
        }
        if (aclBindings.isEmpty()) return;
        CreateAclsResult aclsResult = adminClient.createAcls(aclBindings);
        System.out.println(aclsResult.all().isCompletedExceptionally());
    }

    private AclBinding translateToACL(String user, String topic, Boolean constructive) {
        System.out.println("Creating ACL:  " + user + " " + topic + " " + constructive);
        KafkaPrincipal kafkaPrincipal = new KafkaPrincipal("User", user);
        ResourcePattern  resourcePattern = new ResourcePattern(ResourceType.TOPIC, topic, PatternType.LITERAL);
        AclPermissionType aclPermissionType = constructive ? AclPermissionType.ALLOW : AclPermissionType.DENY;
        return new AclBinding(resourcePattern, new AccessControlEntry(kafkaPrincipal.toString(), "*", AclOperation.READ, aclPermissionType));
    }
}
