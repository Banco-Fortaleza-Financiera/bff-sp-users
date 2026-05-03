package com.bancofortaleza.users.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

class PaginationUtilsTest {

    @Test
    void fromHeadersShouldConvertOneBasedPageToZeroBasedPage() {
        Pageable result = PaginationUtils.fromHeaders(3, 25, Sort.by("id"));

        assertThat(result.getPageNumber()).isEqualTo(2);
        assertThat(result.getPageSize()).isEqualTo(25);
        assertThat(result.getSort().getOrderFor("id")).isNotNull();
    }

    @Test
    void fromHeadersShouldUseDefaultsForNullOrInvalidValues() {
        Pageable nullValues = PaginationUtils.fromHeaders(null, null, Sort.unsorted());
        Pageable invalidValues = PaginationUtils.fromHeaders(0, 0, Sort.unsorted());

        assertThat(nullValues.getPageNumber()).isZero();
        assertThat(nullValues.getPageSize()).isEqualTo(20);
        assertThat(invalidValues.getPageNumber()).isZero();
        assertThat(invalidValues.getPageSize()).isEqualTo(20);
    }

    @Test
    void fromHeadersShouldCapPageSizeAtMaximum() {
        Pageable result = PaginationUtils.fromHeaders(1, 500, Sort.unsorted());

        assertThat(result.getPageSize()).isEqualTo(100);
    }
}
