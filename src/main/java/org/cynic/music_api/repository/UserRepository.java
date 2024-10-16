package org.cynic.music_api.repository;

import org.cynic.music_api.domain.entity.User;
import org.cynic.music_api.domain.entity.User_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long>, JpaSpecificationExecutor<User> {

    static Specification<User> byEmail(String email) {
        return (root, _, criteriaBuilder) -> criteriaBuilder.equal(root.get(User_.email), email);
    }
}
