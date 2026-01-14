package krs.erp.model.finance;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import krs.erp.enums.AccountType;
import krs.erp.model.BaseEntity;

@Entity
@Table(name = "erp_chart_of_accounts")
public class ChartOfAccount extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @ManyToOne
    @JoinColumn(name = "parent_account_id")
    private ChartOfAccount parentAccount;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public ChartOfAccount getParentAccount() {
        return parentAccount;
    }

    public void setParentAccount(ChartOfAccount parentAccount) {
        this.parentAccount = parentAccount;
    }
}
