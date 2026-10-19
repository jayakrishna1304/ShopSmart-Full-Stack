package com.shopsmart.loyalty.service;

import com.shopsmart.loyalty.entity.LoyaltyAccount;
import com.shopsmart.loyalty.repository.LoyaltyAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LoyaltyAccountService {

    @Autowired
    private LoyaltyAccountRepository repository;


    public List<LoyaltyAccount> getAllAccounts() {
        return repository.findAll();
    }


    public Optional<LoyaltyAccount> getAccountById(Integer id) {
        return repository.findById(id);
    }


    public Optional<LoyaltyAccount> getAccountByCustomerId(Integer customerId) {
        return repository.findByCustomerId(customerId);
    }


    /*
     * Automatically get the customer's loyalty account.
     *
     * If the account does not exist, create it with:
     *
     * pointsAvail = 0
     * pointsRedeemed = 0
     * tier = Bronze
     */
    public LoyaltyAccount getOrCreateAccount(Integer customerId) {

        if (customerId == null) {
            throw new RuntimeException(
                    "Customer ID cannot be null."
            );
        }

        Optional<LoyaltyAccount> existingAccount =
                repository.findByCustomerId(customerId);

        if (existingAccount.isPresent()) {
            return existingAccount.get();
        }

        LoyaltyAccount account =
                new LoyaltyAccount();

        account.setCustomerId(customerId);
        account.setPointsAvail(0);
        account.setPointsRedeemed(0);
        account.setTier("Bronze");
        account.setPointsHistory(null);

        return repository.save(account);
    }


    /*
     * Manual account creation is still available.
     *
     * Customer validation is removed because the
     * authenticated JWT identifies the customer.
     */
    public LoyaltyAccount createAccount(
            LoyaltyAccount account) {

        if (account.getCustomerId() == null) {
            throw new RuntimeException(
                    "Customer ID cannot be null."
            );
        }

        Optional<LoyaltyAccount> existingAccount =
                repository.findByCustomerId(
                        account.getCustomerId()
                );

        if (existingAccount.isPresent()) {
            return existingAccount.get();
        }

        if (account.getPointsAvail() == null) {
            account.setPointsAvail(0);
        }

        if (account.getPointsRedeemed() == null) {
            account.setPointsRedeemed(0);
        }

        if (account.getTier() == null
                || account.getTier().isEmpty()) {

            account.setTier("Bronze");
        }

        return repository.save(account);
    }


    /*
     * Add points using loyaltyId.
     */
    public LoyaltyAccount updatePoints(
            Integer id,
            Integer pointsToAdd) {

        if (pointsToAdd == null || pointsToAdd <= 0) {
            throw new RuntimeException(
                    "Points to add must be greater than 0."
            );
        }

        LoyaltyAccount account =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Account not found " + id
                                )
                        );

        int currentPoints =
                account.getPointsAvail() != null
                        ? account.getPointsAvail()
                        : 0;

        int newPointsAvail =
                currentPoints + pointsToAdd;

        account.setPointsAvail(
                newPointsAvail
        );


        /*
         * Update loyalty tier based on available points.
         */
        if (newPointsAvail > 13000) {

            account.setTier("Diamond");

        } else if (newPointsAvail > 9000) {

            account.setTier("Platinum");

        } else if (newPointsAvail > 4500) {

            account.setTier("Gold");

        } else if (newPointsAvail > 2000) {

            account.setTier("Silver");

        } else {

            account.setTier("Bronze");
        }


        String historyMessage =
                pointsToAdd
                        + " points were added to balance.";

        if (account.getPointsHistory() == null
                || account.getPointsHistory().isEmpty()) {

            account.setPointsHistory(
                    historyMessage
            );

        } else {

            account.setPointsHistory(
                    account.getPointsHistory()
                            + " | "
                            + historyMessage
            );
        }


        return repository.save(account);
    }


    /*
     * Add points using customerId.
     *
     * If account doesn't exist,
     * automatically create it first.
     */
    public LoyaltyAccount updatePointsByCustomer(
            Integer customerId,
            Integer pointsToAdd) {

        LoyaltyAccount account =
                getOrCreateAccount(customerId);

        return updatePoints(
                account.getLoyaltyId(),
                pointsToAdd
        );
    }


    public void deleteAccount(Integer id) {
        repository.deleteById(id);
    }


    public Integer getPointsAvail(Integer id) {

        LoyaltyAccount account =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Account not found " + id
                                )
                        );

        return account.getPointsAvail();
    }


    public Integer getPointsRedeemed(Integer id) {

        LoyaltyAccount account =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Account not found " + id
                                )
                        );

        return account.getPointsRedeemed();
    }


    /*
     * Get available points using customerId.
     *
     * If account doesn't exist,
     * automatically create it with 0 points.
     */
    public Integer getPointsAvailByCustomer(
            Integer customerId) {

        LoyaltyAccount account =
                getOrCreateAccount(customerId);

        return account.getPointsAvail();
    }


    /*
     * Redeem points using loyaltyId.
     */
    public LoyaltyAccount redeemPoints(
            Integer id,
            Integer pointsToRedeem) {

        if (pointsToRedeem == null
                || pointsToRedeem <= 0) {

            throw new RuntimeException(
                    "Points to redeem must be greater than 0."
            );
        }

        LoyaltyAccount account =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Loyalty Account not found with ID: "
                                                + id
                                )
                        );


        int availablePoints =
                account.getPointsAvail() != null
                        ? account.getPointsAvail()
                        : 0;


        if (availablePoints < pointsToRedeem) {

            throw new RuntimeException(
                    "Insufficient points available for redemption. "
                            + "Available points: "
                            + availablePoints
            );
        }


        /*
         * Deduct from available points.
         */
        int newPointsAvail =
                availablePoints - pointsToRedeem;

        account.setPointsAvail(
                newPointsAvail
        );


        /*
         * Increase redeemed points.
         */
        int currentRedeemed =
                account.getPointsRedeemed() != null
                        ? account.getPointsRedeemed()
                        : 0;

        account.setPointsRedeemed(
                currentRedeemed + pointsToRedeem
        );


        /*
         * Append redemption history.
         */
        String historyMessage =
                pointsToRedeem
                        + " points were redeemed.";

        if (account.getPointsHistory() == null
                || account.getPointsHistory().isEmpty()) {

            account.setPointsHistory(
                    historyMessage
            );

        } else {

            account.setPointsHistory(
                    account.getPointsHistory()
                            + " | "
                            + historyMessage
            );
        }


        /*
         * Recalculate tier after redemption.
         */
        updateTier(account);


        return repository.save(account);
    }


    /*
     * Redeem points using customerId.
     *
     * If account does not exist,
     * it is automatically created with 0.
     *
     * Therefore attempting to redeem from
     * a new account will correctly fail with
     * insufficient points.
     */
    public LoyaltyAccount redeemPointsByCustomer(
            Integer customerId,
            Integer pointsToRedeem) {

        LoyaltyAccount account =
                getOrCreateAccount(customerId);

        return redeemPoints(
                account.getLoyaltyId(),
                pointsToRedeem
        );
    }


    private void updateTier(
            LoyaltyAccount account) {

        int points =
                account.getPointsAvail() != null
                        ? account.getPointsAvail()
                        : 0;

        if (points > 13000) {

            account.setTier("Diamond");

        } else if (points > 9000) {

            account.setTier("Platinum");

        } else if (points > 4500) {

            account.setTier("Gold");

        } else if (points > 2000) {

            account.setTier("Silver");

        } else {

            account.setTier("Bronze");
        }
    }


    public String getPointsHistory(Integer id) {

        LoyaltyAccount account =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Loyalty Account not found with ID: "
                                                + id
                                )
                        );

        return account.getPointsHistory() != null
                ? account.getPointsHistory()
                : "No points history available.";
    }
}