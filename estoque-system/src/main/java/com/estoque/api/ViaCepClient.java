package com.estoque.api;

import com.estoque.model.Endereco;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Cliente da API REST pública ViaCEP.
 * Endpoint: https://viacep.com.br/ws/{cep}/json/
 */
public class ViaCepClient {

    private static final String BASE_URL = "https://viacep.com.br/ws/%s/json/";
    private static final int TIMEOUT_MS = 8000; // 8 segundos

    private final Gson gson = new Gson();

    /**
     * Busca o endereço completo para o CEP informado.
     *
     * @param cep CEP no formato "12345678" ou "12345-678"
     * @return Endereco preenchido, ou null se CEP inválido
     * @throws IOException se houver falha de rede
     */
    public Endereco buscarEndereco(String cep) throws IOException {
        // Sanitiza: remove qualquer caractere que não seja dígito
        String cepLimpo = cep.replaceAll("\\D", "");

        if (cepLimpo.length() != 8) {
            throw new IllegalArgumentException(
                    "CEP deve ter 8 dígitos numéricos. Recebido: " + cep);
        }

        String urlStr = String.format(BASE_URL, cepLimpo);
        HttpURLConnection conn = null;

        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            conn.setRequestProperty("Accept", "application/json");

            int httpStatus = conn.getResponseCode();

            if (httpStatus != HttpURLConnection.HTTP_OK) {
                throw new IOException(
                        "ViaCEP retornou status HTTP " + httpStatus + " para o CEP " + cep);
            }

            // Lê o corpo da resposta
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {

                String linha;
                // Uso de while (laço de leitura linha a linha)
                while ((linha = reader.readLine()) != null) {
                    sb.append(linha);
                }
            }

            String json = sb.toString();
            JsonObject obj = gson.fromJson(json, JsonObject.class);

            // ViaCEP retorna {"erro": true} para CEPs inválidos
            if (obj.has("erro") && obj.get("erro").getAsBoolean()) {
                return null; // CEP não encontrado
            }

            return montarEndereco(obj, cepLimpo);

        } finally {
            if (conn != null)
                conn.disconnect();
        }
    }

    // ------------------------------------------------------------------
    // Converte JsonObject → Endereco
    // ------------------------------------------------------------------
    private Endereco montarEndereco(JsonObject obj, String cepLimpo) {
        Endereco end = new Endereco();

        // Formata o CEP com hífen: 12345-678
        end.setCep(cepLimpo.substring(0, 5) + "-" + cepLimpo.substring(5));
        end.setLogradouro(getString(obj, "logradouro"));
        end.setComplemento(getString(obj, "complemento"));
        end.setBairro(getString(obj, "bairro"));
        end.setCidade(getString(obj, "localidade"));
        end.setUf(getString(obj, "uf"));

        return end;
    }

    private String getString(JsonObject obj, String campo) {
        if (obj.has(campo) && !obj.get(campo).isJsonNull()) {
            String valor = obj.get(campo).getAsString();
            return valor.isBlank() ? null : valor;
        }
        return null;
    }
}
