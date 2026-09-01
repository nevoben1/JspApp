<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.net.HttpURLConnection" %>
<%@ page import="java.net.URL" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.InputStreamReader" %>
<%
    // --- Do a bit of real work per request so the box can be load-tested. ---
    // Makes N server-side HTTP GETs to a public API. Tune with ?calls=N (default 3, max 20).
    int calls = 3;
    try { calls = Integer.parseInt(request.getParameter("calls").trim()); } catch (Exception e) {}
    calls = Math.max(1, Math.min(calls, 20));

    String api = "https://jsonplaceholder.typicode.com/todos/1";
    long startedAt = System.currentTimeMillis();
    int ok = 0, failed = 0;
    long bytes = 0;

    for (int i = 0; i < calls; i++) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(api).openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) bytes += line.length();
            in.close();
            if (code >= 200 && code < 300) ok++; else failed++;
        } catch (Exception ex) {
            failed++;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    long elapsedMs = System.currentTimeMillis() - startedAt;
%>
<!DOCTYPE html>
<html>
<head>
    <title>Simple JSP App</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f9;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            height: 100vh;
            margin: 0;
        }
        .container {
            background: #fff;
            padding: 30px 40px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            text-align: center;
        }
        input[type=text] {
            padding: 8px;
            width: 200px;
            margin-bottom: 15px;
            border: 1px solid #ccc;
            border-radius: 5px;
        }
        input[type=submit] {
            padding: 8px 20px;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }
        input[type=submit]:hover {
            background-color: #45a049;
        }
        a {
            display: block;
            margin-top: 20px;
            color: #4CAF50;
            text-decoration: none;
        }
        a:hover {
            text-decoration: underline;
        }
        .greeting {
            margin-top: 20px;
            font-size: 18px;
            color: #333;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Welcome to My Simple Web App</h1>

        <form action="index.jsp" method="get">
            <input type="text" name="username" placeholder="Enter your name" />
            <br/>
            <input type="submit" value="Say Hello" />
        </form>

        <%
            String name = request.getParameter("username");
            if (name != null && !name.trim().isEmpty()) {
        %>
            <div class="greeting">
                Hello, <b><%= name %></b>! Welcome to the app.
            </div>
        <%
            }
        %>

        <a href="https://github.com/" target="_blank">Visit my GitHub</a>
    </div>
</body>
</html>
