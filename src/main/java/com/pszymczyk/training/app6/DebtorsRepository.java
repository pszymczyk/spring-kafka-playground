package com.pszymczyk.training.app6;

import java.util.Set;

public class DebtorsRepository {

    public Set<String> getDebtors() {
        return Set.of(
                "jan k.",
                "wincenty f.",
                "zenon j.",
                "amanda .d",
                "teresa b.",
                "anna g."
        );
    }

    public Set<String> getBlackList() {
        return Set.of(
                "andrzej k.",
                "jacek b.",
                "marzena j.",
                "pawel s.",
                "lucyna j.",
                "waldemar g."
        );
    }
}
