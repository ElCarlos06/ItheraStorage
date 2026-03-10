import { useState, useRef, useCallback } from "react";
import { createPortal } from "react-dom";
import "./Tooltip.css";

const SHOW_DELAY = 350;
const HIDE_DELAY = 80;

export default function Tooltip({
  content,
  children,
  followCursor = true,
  placement = "bottom",
  className = "",
  as: Wrapper = "div",
}) {
  const [visible, setVisible] = useState(false);
  const [pos, setPos] = useState({ x: 0, y: 0 });
  const showTimeout = useRef(null);
  const hideTimeout = useRef(null);

  const clearTimers = useCallback(() => {
    if (showTimeout.current) clearTimeout(showTimeout.current);
    if (hideTimeout.current) clearTimeout(hideTimeout.current);
    showTimeout.current = null;
    hideTimeout.current = null;
  }, []);

  const handleEnter = useCallback(
    (e) => {
      clearTimers();
      if (followCursor) {
        setPos({ x: e.clientX + 14, y: e.clientY + 10 });
      }
      showTimeout.current = setTimeout(() => setVisible(true), SHOW_DELAY);
    },
    [followCursor, clearTimers]
  );

  const handleMove = useCallback(
    (e) => {
      if (followCursor) {
        setPos({ x: e.clientX + 14, y: e.clientY + 10 });
      }
    },
    [followCursor]
  );

  const handleLeave = useCallback(() => {
    clearTimers();
    hideTimeout.current = setTimeout(() => setVisible(false), HIDE_DELAY);
  }, [clearTimers]);

  const tooltipEl = visible && (
    <span
      className={`tooltip tooltip--follow tooltip--${placement}`}
      style={followCursor ? { left: pos.x, top: pos.y } : undefined}
      role="tooltip"
    >
      {content}
    </span>
  );

  return (
    <>
      <Wrapper
        className={`tooltip-trigger ${className}`.trim()}
        onMouseEnter={handleEnter}
        onMouseMove={handleMove}
        onMouseLeave={handleLeave}
      >
        {children}
        {tooltipEl && createPortal(tooltipEl, document.body)}
      </Wrapper>
    </>
  );
}
