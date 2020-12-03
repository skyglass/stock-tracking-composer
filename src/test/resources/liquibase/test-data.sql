insert into BUSINESSOWNER (UUID, NAME) values ('6018d2e1-b94b-424a-840c-9cbae9074f4e', 'BusinessOwner1');
insert into BUSINESSOWNER (UUID, NAME) values ('befd22b7-53d0-4671-9df7-49dbbf38e45e', 'BusinessOwner2');

insert into "USER" (UUID, USERNAME, OWNER_UUID) values ('02655648-7238-48e5-a36e-45025559b219', 'USER1', '6018d2e1-b94b-424a-840c-9cbae9074f4e');
insert into "USER" (UUID, USERNAME, OWNER_UUID) values ('0e893b6f-1495-4d62-9c1a-abf5c9cc281f', 'USER2', '6018d2e1-b94b-424a-840c-9cbae9074f4e');

insert into BUSINESSUNIT (UUID, NAME, OWNER_UUID, PARENT_UUID) values ('158d60d5-5a81-4b1f-b7d6-36a349e05082', 'Stock Center', '6018d2e1-b94b-424a-840c-9cbae9074f4e', null);
insert into BUSINESSUNIT (UUID, NAME, OWNER_UUID, PARENT_UUID) values ('DF789ACB-0CC3-4B4C-BF73-1E68DE4C7CA4', 'BusinessUnit1', '6018d2e1-b94b-424a-840c-9cbae9074f4e', null);
insert into BUSINESSUNIT (UUID, NAME, OWNER_UUID, PARENT_UUID) values ('d659dd95-c3b7-4f55-adf0-596a117c12b9', 'BusinessUnit2', '6018d2e1-b94b-424a-840c-9cbae9074f4e', null);

insert into BUSINESSUNITHIERARCHY (UUID, CHILD_UUID, PARENT_UUID) values ('34fcf583-b396-4c68-af36-73cbaaac9a2d', '158d60d5-5a81-4b1f-b7d6-36a349e05082', '158d60d5-5a81-4b1f-b7d6-36a349e05082');
insert into BUSINESSUNITHIERARCHY (UUID, CHILD_UUID, PARENT_UUID) values ('a59c7d16-bb97-4383-9e5f-c62795931b8d', 'DF789ACB-0CC3-4B4C-BF73-1E68DE4C7CA4', 'DF789ACB-0CC3-4B4C-BF73-1E68DE4C7CA4');	
insert into BUSINESSUNITHIERARCHY (UUID, CHILD_UUID, PARENT_UUID) values ('6d717998-e1fe-41c6-ba39-c273d270023c', 'd659dd95-c3b7-4f55-adf0-596a117c12b9', 'd659dd95-c3b7-4f55-adf0-596a117c12b9');		

insert into ITEM (UUID, NAME) values ('9f797b73-ffbe-41c5-b7ed-453d450a7ef4', 'Item1');
insert into ITEM (UUID, NAME) values ('34034833-b32b-40ad-928f-eef12c9dbe2c', 'Item2');