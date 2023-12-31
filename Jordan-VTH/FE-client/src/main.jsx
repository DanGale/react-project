import React from "react";
import ReactDOM from "react-dom/client";
import { NextUIProvider } from "@nextui-org/react";
import { BrowserRouter } from "react-router-dom";
import { PrimeReactProvider } from "primereact/api";
import App from "./App.jsx";

import "./index.css";

ReactDOM.createRoot(document.getElementById("root")).render(
  <BrowserRouter>
    <React.StrictMode>
      <NextUIProvider>
        <PrimeReactProvider>
          <App />
        </PrimeReactProvider>
      </NextUIProvider>
    </React.StrictMode>
  </BrowserRouter>
);
