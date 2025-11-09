package davidebraghi.CapstoneProject_TimelineManager.repositories.Specifications;

import davidebraghi.CapstoneProject_TimelineManager.entities.Task;
import davidebraghi.CapstoneProject_TimelineManager.enums.TaskPriorityENUM;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskSpecification {

    public static Specification<Task> buildSpecification(
            Long projectId,
            Long statusId,
            TaskPriorityENUM taskPriority,
            Long assigneeId,
            Boolean isCompleted,
            Boolean isOverdue,
            String search,
            LocalDate createdAfter,
            LocalDate createdBefore,
            LocalDate expiryDateBefore,
            LocalDate expiryDateAfter,
            Long excludeStatusId,
            TaskPriorityENUM excludePriority,
            String createdWithinLast,
            String expiringIn,
            Boolean createdThisWeek,
            Boolean createdThisMonth
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // filtro Project

            if (projectId != null) {
                predicates.add(criteriaBuilder.equal(root.get("project").get("projectId"), projectId));
            }

            // filtro Status

            if (statusId != null) {
                predicates.add(criteriaBuilder.equal(root.get("status").get("statusId"), statusId));
            }

            // filtro Priority

            if (taskPriority != null) {
                predicates.add(criteriaBuilder.equal(root.get("taskPriority"), taskPriority));
            }

            // filtro Completed

            if (isCompleted != null) {
                if (isCompleted) {
                    predicates.add(criteriaBuilder.isNotNull(root.get("completedAt")));
                } else {
                    predicates.add(criteriaBuilder.isNull(root.get("completedAt")));
                }
            }

            // filtro ExpiryDate

            if (isOverdue != null && isOverdue) {
                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.isNotNull(root.get("taskExpiryDate")),
                        criteriaBuilder.lessThan(root.get("taskExpiryDate"), LocalDate.now()),
                        criteriaBuilder.isNull(root.get("completedAt"))
                ));
            }

            // ricerca testuale

            if (search != null && !search.isBlank()) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("taskTitle")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("taskDescription")), searchPattern)
                ));
            }

            // filtri data creazione

            if (createdAfter != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), createdAfter));
            }
            if (createdBefore != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), createdBefore));
            }

            // filtri data scadenza
            if (expiryDateBefore != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("taskExpiryDate"), expiryDateBefore));
            }
            if (expiryDateAfter != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("taskExpiryDate"), expiryDateAfter));
            }

            // filtro Assignee (se hai Task_Assignee join)

            if (assigneeId != null) {
                predicates.add(criteriaBuilder.isMember(
                        assigneeId,
                        root.get("assignees")
                ));
            }

            if (excludeStatusId != null) {
                predicates.add(criteriaBuilder.notEqual(root.get("status").get("statusId"), excludeStatusId));
            }
            if (excludePriority != null) {
                predicates.add(criteriaBuilder.notEqual(root.get("taskPriority"), excludePriority));
            }

            // filtri temporali dinamici

            LocalDate today = LocalDate.now();

            if (createdWithinLast != null) {
                LocalDate threshold = null;
                if (createdWithinLast.equals("7days")) {
                    threshold = today.minusDays(7);
                } else if (createdWithinLast.equals("30days")) {
                    threshold = today.minusDays(30);
                } else if (createdWithinLast.equals("90days")) {
                    threshold = today.minusDays(90);
                }
                if (threshold != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), threshold));
                }
            }

            if (expiringIn != null) {
                LocalDate expiryThreshold = null;
                if (expiringIn.equals("3days")) {
                    expiryThreshold = today.plusDays(3);
                } else if (expiringIn.equals("7days")) {
                    expiryThreshold = today.plusDays(7);
                } else if (expiringIn.equals("30days")) {
                    expiryThreshold = today.plusDays(30);
                }
                if (expiryThreshold != null) {
                    predicates.add(criteriaBuilder.and(
                            criteriaBuilder.greaterThanOrEqualTo(root.get("taskExpiryDate"), today),
                            criteriaBuilder.lessThanOrEqualTo(root.get("taskExpiryDate"), expiryThreshold),
                            criteriaBuilder.isNull(root.get("completedAt"))
                    ));
                }
            }

            if (createdThisWeek != null && createdThisWeek) {
                LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
                LocalDate weekEnd = weekStart.plusDays(6);
                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), weekStart),
                        criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), weekEnd)
                ));
            }

            if (createdThisMonth != null && createdThisMonth) {
                LocalDate monthStart = today.withDayOfMonth(1);
                LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());
                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), monthStart),
                        criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), monthEnd)
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}