package com.estoque.api;

import com.estoque.model.Endereco;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

final class ViaCepResposta {
    String logradouro;
    String complemento;
    String bairro;
    @SerializedName("localidade")
    String cidade;
    String uf;
    boolean erro;
}

public class ViaCepClient {

    private static final String BASE_URL = "https://viacep.com.br/ws/%s/json/";
    private static final int TIMEOUT_MS = 8000;
    private static final Gson GSON = new Gson();

    public Endereco buscarEndereco(String cep) throws IOException {

        String cepLimpo = cep.replaceAll("\\D", "");

        if (cepLimpo.length() != 8) {
            throw new IllegalArgumentException(
                    "CEP deve ter 8 dígitos numéricos. Recebido: " + cep);
        }

        String urlStr = String.format(BASE_URL, cepLimpo);
        HttpURLConnection conn = null;

        try {
            URL url = URI.create(urlStr).toURL();
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            conn.setRequestProperty("Accept", "application/json");

            int httpStatus = conn.getResponseCode();
            if (httpStatus != HttpURLConnection.HTTP_OK) {
                throw new IOException("ViaCEP retornou status HTTP " + httpStatus + " para o CEP " + cep);
            }

            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String linha;
                while ((linha = reader.readLine()) != null) {
                    sb.append(linha);
                }
            }

            ViaCepResposta resposta = GSON.fromJson(sb.toString(), ViaCepResposta.class);

            if (resposta == null || resposta.erro) {
                return null;
            }

            return montarEndereco(resposta, cepLimpo);

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private Endereco montarEndereco(ViaCepResposta r, String cepLimpo) {
        Endereco end = new Endereco();
        end.setCep(cepLimpo.substring(0, 5) + "-" + cepLimpo.substring(5));
        end.setLogradouro(nullSeVazio(r.logradouro));
        end.setComplemento(nullSeVazio(r.complemento));
        end.setBairro(nullSeVazio(r.bairro));
        end.setCidade(nullSeVazio(r.cidade));
        end.setUf(nullSeVazio(r.uf));
        return end;
    }

    private String nullSeVazio(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }
}
