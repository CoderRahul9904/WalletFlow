package com.rsm.walletflow.integration;

import com.rsm.walletflow.dto.CreateUserRequest;
import com.rsm.walletflow.dto.UserTransferMoneyRequest;
import com.rsm.walletflow.entity.User;
import com.rsm.walletflow.entity.Wallet;
import com.rsm.walletflow.repository.UserRepository;
import com.rsm.walletflow.repository.WalletRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end REST API tests running against an isolated in-memory H2 database.
 * These encode the INTENDED correct behaviour (status codes, validation, atomicity).
 */
@SpringBootTest
@AutoConfigureMockMvc
class WalletFlowApiIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired WalletRepository walletRepository;

    private static final AtomicLong SEQ = new AtomicLong(1000);

    /** Seed a user + wallet directly through repositories (isolates setup from the buggy create-user endpoint). */
    private Wallet newWallet(String name) {
        long n = SEQ.incrementAndGet();
        User user = User.builder()
                .name(name)
                .email("user" + n + "@test.com")
                .passwordHash("HASH_" + n)
                .phone("9000000" + n)
                .createdAt(LocalDateTime.now())
                .build();
        user = userRepository.save(user);
        Wallet wallet = Wallet.builder()
                .user(user)
                .balance(BigDecimal.ZERO)
                .currency("INR")
                .createdAt(LocalDateTime.now())
                .build();
        return walletRepository.save(wallet);
    }

    private BigDecimal balanceOf(Long walletId) {
        return walletRepository.getWalletById(walletId).orElseThrow().getBalance();
    }

    @Nested
    @DisplayName("User APIs")
    class UserApi {

        @Test
        @DisplayName("POST /users persists the password into passwordHash (bug B-1)")
        void createUserPersistsPassword() throws Exception {
            CreateUserRequest req = CreateUserRequest.builder()
                    .name("Neo").email("neo@test.com").password("secret123").phone("999").build();

            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Neo"));

            User saved = userRepository.getUserByEmail("neo@test.com").orElseThrow();
            assertThat(saved.getPasswordHash()).isEqualTo("secret123");
        }

        @Test
        @DisplayName("GET /users/{id} returns the user; unknown id -> 404")
        void getUserById() throws Exception {
            Wallet w = newWallet("Trinity");
            mockMvc.perform(get("/api/v1/users/" + w.getUser().getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Trinity"));

            mockMvc.perform(get("/api/v1/users/99999999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GET /users?email= returns the user; unknown email -> 404")
        void getUserByEmail() throws Exception {
            Wallet w = newWallet("Morpheus");
            mockMvc.perform(get("/api/v1/users").param("email", w.getUser().getEmail()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Morpheus"));

            mockMvc.perform(get("/api/v1/users").param("email", "ghost@nope.com"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Wallet APIs")
    class WalletApi {

        @Test
        @DisplayName("POST /wallets/users/{id} creates a zero-balance wallet; duplicate is rejected")
        void createAndDuplicateWallet() throws Exception {
            long n = SEQ.incrementAndGet();
            User user = userRepository.save(User.builder()
                    .name("Dup").email("dup" + n + "@test.com").passwordHash("h").phone("1")
                    .createdAt(LocalDateTime.now()).build());

            mockMvc.perform(post("/api/v1/wallets/users/" + user.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.balance").value(0))
                    .andExpect(jsonPath("$.currency").value("INR"));

            // second creation must be rejected (business rule prohibits duplicate wallets)
            mockMvc.perform(post("/api/v1/wallets/users/" + user.getId()))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("GET /wallets/{id}/balance returns balance; invalid id -> 404")
        void getBalance() throws Exception {
            Wallet w = newWallet("Bal");
            mockMvc.perform(get("/api/v1/wallets/" + w.getId() + "/balance"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.currency").value("INR"));

            mockMvc.perform(get("/api/v1/wallets/424242/balance"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deposit increases balance and records an INCOMING DEPOSIT")
        void depositValid() throws Exception {
            Wallet w = newWallet("Dep");
            mockMvc.perform(post("/api/v1/wallets/" + w.getId() + "/deposit")
                            .contentType(MediaType.APPLICATION_JSON).content("500.00"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalAmount").value(500.00))
                    .andExpect(jsonPath("$.depositAmount").value(500.00));

            assertThat(balanceOf(w.getId())).isEqualByComparingTo("500.00");
        }

        @Test
        @DisplayName("Deposit of zero / negative / null is rejected with 400")
        void depositInvalid() throws Exception {
            Wallet w = newWallet("DepBad");
            for (String body : new String[]{"0", "-100", "null"}) {
                mockMvc.perform(post("/api/v1/wallets/" + w.getId() + "/deposit")
                                .contentType(MediaType.APPLICATION_JSON).content(body))
                        .andExpect(status().isBadRequest());
            }
            assertThat(balanceOf(w.getId())).isEqualByComparingTo("0");
        }

        @Test
        @DisplayName("Withdraw decreases balance; exceeding balance -> 400; negative -> 400 (bug B-3)")
        void withdraw() throws Exception {
            Wallet w = newWallet("Wd");
            mockMvc.perform(post("/api/v1/wallets/" + w.getId() + "/deposit")
                    .contentType(MediaType.APPLICATION_JSON).content("300"));

            // valid
            mockMvc.perform(post("/api/v1/wallets/" + w.getId() + "/withdraw")
                            .contentType(MediaType.APPLICATION_JSON).content("100"))
                    .andExpect(status().isOk());
            assertThat(balanceOf(w.getId())).isEqualByComparingTo("200");

            // exceeds balance
            mockMvc.perform(post("/api/v1/wallets/" + w.getId() + "/withdraw")
                            .contentType(MediaType.APPLICATION_JSON).content("9999"))
                    .andExpect(status().isBadRequest());

            // negative withdrawal must NOT increase the balance
            mockMvc.perform(post("/api/v1/wallets/" + w.getId() + "/withdraw")
                            .contentType(MediaType.APPLICATION_JSON).content("-50"))
                    .andExpect(status().isBadRequest());
            assertThat(balanceOf(w.getId())).isEqualByComparingTo("200");
        }
    }

    @Nested
    @DisplayName("Transfer APIs")
    class TransferApi {

        private String body(Long receiverId, String amount) throws Exception {
            UserTransferMoneyRequest r = new UserTransferMoneyRequest();
            r.setReceiverWalletId(receiverId);
            r.setAmount(new BigDecimal(amount));
            return objectMapper.writeValueAsString(r);
        }

        @Test
        @DisplayName("Valid transfer moves money and both histories reflect direction")
        void transferValid() throws Exception {
            Wallet sender = newWallet("Sender");
            Wallet receiver = newWallet("Receiver");
            mockMvc.perform(post("/api/v1/wallets/" + sender.getId() + "/deposit")
                    .contentType(MediaType.APPLICATION_JSON).content("1000"));

            mockMvc.perform(post("/api/v1/transactions/transfer/" + sender.getId())
                            .contentType(MediaType.APPLICATION_JSON).content(body(receiver.getId(), "250")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.transferAmount").value(250));

            assertThat(balanceOf(sender.getId())).isEqualByComparingTo("750");
            assertThat(balanceOf(receiver.getId())).isEqualByComparingTo("250");

            // sender history: OUTGOING transfer with counterparty = Receiver
            mockMvc.perform(post("/api/v1/transactions/history/" + sender.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[?(@.transactionType == 'TRANSFER' && @.walletMoneyFlowType == 'OUTGOING' && @.counterPartyName == 'Receiver')]").exists());

            // receiver history: INCOMING transfer with counterparty = Sender
            mockMvc.perform(post("/api/v1/transactions/history/" + receiver.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[?(@.transactionType == 'TRANSFER' && @.walletMoneyFlowType == 'INCOMING' && @.counterPartyName == 'Sender')]").exists());
        }

        @Test
        @DisplayName("Zero / negative / self transfers are rejected (bugs B-4, B-5)")
        void transferInvalid() throws Exception {
            Wallet sender = newWallet("T1");
            Wallet receiver = newWallet("T2");
            mockMvc.perform(post("/api/v1/wallets/" + sender.getId() + "/deposit")
                    .contentType(MediaType.APPLICATION_JSON).content("100"));

            mockMvc.perform(post("/api/v1/transactions/transfer/" + sender.getId())
                            .contentType(MediaType.APPLICATION_JSON).content(body(receiver.getId(), "0")))
                    .andExpect(status().isBadRequest());

            mockMvc.perform(post("/api/v1/transactions/transfer/" + sender.getId())
                            .contentType(MediaType.APPLICATION_JSON).content(body(receiver.getId(), "-10")))
                    .andExpect(status().isBadRequest());

            // self transfer
            mockMvc.perform(post("/api/v1/transactions/transfer/" + sender.getId())
                            .contentType(MediaType.APPLICATION_JSON).content(body(sender.getId(), "10")))
                    .andExpect(status().isBadRequest());

            // exceeds balance
            mockMvc.perform(post("/api/v1/transactions/transfer/" + sender.getId())
                            .contentType(MediaType.APPLICATION_JSON).content(body(receiver.getId(), "500")))
                    .andExpect(status().isBadRequest());

            // balances untouched
            assertThat(balanceOf(sender.getId())).isEqualByComparingTo("100");
            assertThat(balanceOf(receiver.getId())).isEqualByComparingTo("0");
        }

        @Test
        @DisplayName("Transfer to a nonexistent receiver rolls back the sender balance")
        void transferRollback() throws Exception {
            Wallet sender = newWallet("Roll");
            mockMvc.perform(post("/api/v1/wallets/" + sender.getId() + "/deposit")
                    .contentType(MediaType.APPLICATION_JSON).content("500"));

            mockMvc.perform(post("/api/v1/transactions/transfer/" + sender.getId())
                            .contentType(MediaType.APPLICATION_JSON).content("{\"receiverWalletId\":98765432,\"amount\":100}"))
                    .andExpect(status().isNotFound());

            assertThat(balanceOf(sender.getId())).isEqualByComparingTo("500");
        }
    }

    @Nested
    @DisplayName("Transaction History")
    class HistoryApi {

        @Test
        @DisplayName("Empty wallet returns an empty list; unknown wallet -> 404")
        void emptyAndUnknown() throws Exception {
            Wallet w = newWallet("Empty");
            mockMvc.perform(post("/api/v1/transactions/history/" + w.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));

            mockMvc.perform(post("/api/v1/transactions/history/55566677"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deposit and withdrawal are labelled with correct flow type")
        void depositWithdrawTypes() throws Exception {
            Wallet w = newWallet("Mix");
            mockMvc.perform(post("/api/v1/wallets/" + w.getId() + "/deposit")
                    .contentType(MediaType.APPLICATION_JSON).content("400"));
            mockMvc.perform(post("/api/v1/wallets/" + w.getId() + "/withdraw")
                    .contentType(MediaType.APPLICATION_JSON).content("100"));

            mockMvc.perform(post("/api/v1/transactions/history/" + w.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[?(@.transactionType == 'DEPOSIT' && @.walletMoneyFlowType == 'INCOMING')]").exists())
                    .andExpect(jsonPath("$[?(@.transactionType == 'WITHDRAWAL' && @.walletMoneyFlowType == 'OUTGOING')]").exists());
        }
    }
}
