package com.bancofortaleza.users.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

class SpecificationUtilsTest {

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void equalIfNotNullShouldUseConjunctionWhenValueIsNull() {
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        Predicate conjunction = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);

        Predicate result = SpecificationUtils.equalIfNotNull("status", null)
                .toPredicate(mock(Root.class), mock(CriteriaQuery.class), criteriaBuilder);

        assertThat(result).isSameAs(conjunction);
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void equalIfNotNullShouldResolveNestedPathWhenValueExists() {
        Root<Object> root = mock(Root.class);
        Path<Object> userPath = mock(Path.class);
        Path<Object> idPath = mock(Path.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        Predicate equalPredicate = mock(Predicate.class);
        when(root.get("user")).thenReturn(userPath);
        when(userPath.get("id")).thenReturn(idPath);
        when(criteriaBuilder.equal(idPath, 7)).thenReturn(equalPredicate);

        Predicate result = SpecificationUtils.equalIfNotNull("user.id", 7)
                .toPredicate(root, mock(CriteriaQuery.class), criteriaBuilder);

        assertThat(result).isSameAs(equalPredicate);
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void containsIgnoreCaseShouldUseConjunctionWhenSearchIsBlank() {
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        Predicate conjunction = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);

        Predicate result = SpecificationUtils.containsIgnoreCase("   ", "name")
                .toPredicate(mock(Root.class), mock(CriteriaQuery.class), criteriaBuilder);

        assertThat(result).isSameAs(conjunction);
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void containsIgnoreCaseShouldBuildOrPredicateForProvidedFields() {
        Root<Object> root = mock(Root.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        Path<Object> namePath = mock(Path.class);
        Path<Object> lastNamePath = mock(Path.class);
        Expression<String> nameExpression = mock(Expression.class);
        Expression<String> lastNameExpression = mock(Expression.class);
        Expression<String> lowerNameExpression = mock(Expression.class);
        Expression<String> lowerLastNameExpression = mock(Expression.class);
        Predicate namePredicate = mock(Predicate.class);
        Predicate lastNamePredicate = mock(Predicate.class);
        Predicate orPredicate = mock(Predicate.class);
        when(root.get("name")).thenReturn(namePath);
        when(root.get("lastName")).thenReturn(lastNamePath);
        when(namePath.as(String.class)).thenReturn(nameExpression);
        when(lastNamePath.as(String.class)).thenReturn(lastNameExpression);
        when(criteriaBuilder.lower(nameExpression)).thenReturn(lowerNameExpression);
        when(criteriaBuilder.lower(lastNameExpression)).thenReturn(lowerLastNameExpression);
        when(criteriaBuilder.like(lowerNameExpression, "%ger%")).thenReturn(namePredicate);
        when(criteriaBuilder.like(lowerLastNameExpression, "%ger%")).thenReturn(lastNamePredicate);
        when(criteriaBuilder.or(any(Predicate[].class))).thenReturn(orPredicate);

        Specification<Object> specification = SpecificationUtils.containsIgnoreCase(" Ger ", "name", "lastName");
        Predicate result = specification.toPredicate(root, mock(CriteriaQuery.class), criteriaBuilder);

        assertThat(result).isSameAs(orPredicate);
        verify(criteriaBuilder).or(any(Predicate[].class));
    }
}
