import * as RadixTooltip from "@radix-ui/react-tooltip";
import "./Tooltip.css";

/**
 * Tooltip usando Radix UI. Estilo blanco con sombra suave.
 */
export function TooltipProvider({ children, delayDuration = 300, ...props }) {
  return (
    <RadixTooltip.Provider delayDuration={delayDuration} {...props}>
      {children}
    </RadixTooltip.Provider>
  );
}

export function Tooltip({ children, content, side = "top", sideOffset = 8 }) {
  return (
    <RadixTooltip.Root>
      <RadixTooltip.Trigger asChild>{children}</RadixTooltip.Trigger>
      <RadixTooltip.Portal>
        <RadixTooltip.Content
          className="radix-tooltip-content"
          side={side}
          sideOffset={sideOffset}
        >
          {content}
          <RadixTooltip.Arrow className="radix-tooltip-arrow" />
        </RadixTooltip.Content>
      </RadixTooltip.Portal>
    </RadixTooltip.Root>
  );
}
