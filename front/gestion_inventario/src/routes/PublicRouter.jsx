import React, { useRef } from "react";
import { Routes, Route, Navigate, useLocation } from "react-router-dom";
import { TransitionGroup, CSSTransition } from "react-transition-group";

import Login from "../modules/public/pages/Login";
import PasswordRecovery from "../modules/public/pages/PasswordRecovery";
import ResetPassword from "../modules/public/pages/ResetPassword";

import "./PublicRouter.css";

const ROUTE_ORDER = ["/login", "/password-recovery", "/reset-password"];

export default function PublicRouter() {
  const location = useLocation();

  // Guardamos la última ruta y dirección para calcularla de forma síncrona en el render
  const prevPathRef = useRef(location.pathname);
  const prevDirectionRef = useRef("forward");

  if (location.pathname !== prevPathRef.current) {
    let currentIndex = ROUTE_ORDER.indexOf(location.pathname);
    if (currentIndex === -1) currentIndex = 0;

    let prevIndex = ROUTE_ORDER.indexOf(prevPathRef.current);
    if (prevIndex === -1) prevIndex = 0;

    prevDirectionRef.current = currentIndex >= prevIndex ? "forward" : "back";
    prevPathRef.current = location.pathname;
  }

  const direction = prevDirectionRef.current;

  // 1. Diccionario para guardar refs individuales por ruta (para corrección de refs)
  const nodeRefs = useRef({});

  // Si no existe un ref para esta ruta, lo creamos
  if (!nodeRefs.current[location.pathname]) {
    nodeRefs.current[location.pathname] = React.createRef();
  }
  const currentNodeRef = nodeRefs.current[location.pathname];

  return (
    <div className="page-overflow-wrapper">
      <TransitionGroup 
        component={null}
        // childFactory es clave: fuerza a que el componente que SALE
        // tome la misma dirección que el componente que ENTRA.
        childFactory={(child) =>
          React.cloneElement(child, {
            classNames: `page-${direction}`,
          })
        }
      >
        <CSSTransition
          key={location.pathname}
          classNames={`page-${direction}`}
          timeout={300} // Sincronizado con el CSS (más rápido y fluido)
          unmountOnExit
          nodeRef={currentNodeRef}
        >
          <div className="page-wrapper" ref={currentNodeRef}>
            <Routes location={location}>
              <Route path="/" element={<Navigate to="/login" replace />} />
              <Route path="/login" element={<Login />} />
              <Route path="/password-recovery" element={<PasswordRecovery />} />
              <Route path="/reset-password" element={<ResetPassword />} />
              <Route path="*" element={<Navigate to="/login" replace />} />
            </Routes>
          </div>
        </CSSTransition>
      </TransitionGroup>
    </div>
  );
}
