Create a Many-to-Many relationship with Micronaut data between the entity ${entityA} and the entity {entityB}.

Given an application with two entities, `User` and `Role`, such as:

```java
package example.micronaut;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;

@MappedEntity("users") 
public record UserEntity(@Nullable
                   @Id 
                   @GeneratedValue 
                   Long id,

                   @NotBlank 
                   String username
                   )  {
}
```

And:

```java
package example.micronaut;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;

@MappedEntity 
public record Role(@Nullable
                   @Id 
                   @GeneratedValue 
                   Long id,

                   @NotBlank 
                   String authority) {
}
```

To create a many-to-many association between `User` and `Role`, we need to create a record for the composite primary key of the new table:

```java
package example.micronaut;

import io.micronaut.data.annotation.Embeddable;
import io.micronaut.data.annotation.Relation;

import java.util.Objects;

@Embeddable 
public class UserRoleId {

    @Relation(value = Relation.Kind.MANY_TO_ONE) 
    private final UserEntity user;

    @Relation(value = Relation.Kind.MANY_TO_ONE) 
    private final Role role;

    public UserRoleId(UserEntity user, Role role) {
        this.user = user;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserRoleId userRoleId = (UserRoleId) o;
        return role.id().equals(userRoleId.getRole().id()) &&
                user.id().equals(userRoleId.getUser().id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(role.id(), user.id());
    }

    public UserEntity getUser() {
        return user;
    }

    public Role getRole() {
        return role;
    }
}
```

And an  entity for the extra table:

```
package example.micronaut;

import io.micronaut.data.annotation.EmbeddedId;
import io.micronaut.data.annotation.MappedEntity;

@MappedEntity 
public class UserRole {

    @EmbeddedId 
    private final UserRoleId id;

    public UserRole(UserRoleId id) {
        this.id = id;
    }

    public UserRole(UserEntity user, Role role) {
        this(new UserRoleId(user, role));
    }

    public UserRoleId getId() {
        return id;
    }
}
```

If the application uses Liquibase for managing the database schema. You will need to add a change set for the new table:

```xml
<?xml version="1.0" encoding="UTF-8"?>

<databaseChangeLog
  xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
         http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.1.xsd">
  <changeSet id="99" author="username">

      <createTable tableName="user_role">
          <column name="id_user_id" type="BIGINT">
              <constraints nullable="false"/>
          </column>
          <column name="id_role_id" type="BIGINT">
              <constraints nullable="false"/>
          </column>
      </createTable>

      <addPrimaryKey tableName="user_role" constraintName="pk_user_role" columnNames="id_user_id, id_role_id"/>

      <addForeignKeyConstraint
              baseTableName="user_role"
              baseColumnNames="id_user_id"
              constraintName="fk_user_role_user"
              referencedTableName="users"
              referencedColumnNames="id"
              onDelete="CASCADE"/>

      <addForeignKeyConstraint
              baseTableName="user_role"
              baseColumnNames="id_role_id"
              constraintName="fk_user_role_role"
              referencedTableName="role"
              referencedColumnNames="id"
              onDelete="CASCADE"/>
  </changeSet>
</databaseChangeLog>
```
