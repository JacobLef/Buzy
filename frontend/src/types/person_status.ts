export const PersonStatus = {
  ACTIVE: "ACTIVE",
  INACTIVE: "INACTIVE",
  ON_LEAVE: "ON_LEAVE",
} as const;

export type PersonStatus = typeof PersonStatus[keyof typeof PersonStatus];
