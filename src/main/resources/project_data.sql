insert into category (category_name) values ('Doors and Windows');
insert into category (category_name) values ('Beds');
insert into category (category_name) values ('Shoes');
insert into category (category_name) values ('Books');

-- Hanging Door
insert into project (project_name, estimated_hours, actual_hours, difficulty, notes) values ('Hang a door', 4, 3, 3, 'Use the door hangers from Home Depot');
insert into project (project_name, estimated_hours, actual_hours, difficulty, notes) values ('Hang a door', 4, 3, 3, 'Use the door hangers from Home Depot');
insert into project (project_name, estimated_hours, actual_hours, difficulty, notes) values ('Hang a closet door', 4.5, 3.5, 4, 'Use the door hangers from Home Depot');


INSERT INTO material (project_id, material_name, num_required, cost) values (1, '2-inch screws', 20, 1.09);
INSERT INTO material (project_id, material_name, num_required, cost) values (2, '2-inch screws', 20, 1.09);
INSERT INTO material (project_id, material_name, num_required, cost) values (3, '2-inch screws', 20, 1.09);
INSERT INTO material (project_id, material_name, num_required, cost) values (1, 'Door', 1, 100);
INSERT INTO material (project_id, material_name, num_required, cost) values (2, 'Door', 1, 100);
INSERT INTO material (project_id, material_name, num_required, cost) values (3, 'Door', 1, 100);

INSERT INTO step (project_id, step_text, step_order) values (1, 'Screw door hangers on the top and bottom of each side of the door frame', 1);
INSERT INTO step (project_id, step_text, step_order) values (1, 'Screw door onto hangers', 2);
INSERT INTO step (project_id, step_text, step_order) values (2, 'Screw door hangers on the top and bottom of each side of the door frame', 1);
INSERT INTO step (project_id, step_text, step_order) values (2, 'Screw door onto hangers', 2);
INSERT INTO step (project_id, step_text, step_order) values (3, 'Screw door hangers on the top and bottom of each side of the door frame', 1);
INSERT INTO step (project_id, step_text, step_order) values (3, 'Screw door onto hangers', 2);

INSERT INTO project_category (project_id, category_id) values (1, 1);
INSERT INTO project_category (project_id, category_id) values (2, 1);
INSERT INTO project_category (project_id, category_id) values (3, 1);

-- making bed
insert into project (project_name, estimated_hours, actual_hours, difficulty, notes) values ('Make Bed', 0.25, 0.25, 1, 'You should know how to do this, but flatten sheets on your bead one at a time.');

INSERT INTO material (project_id, material_name, num_required, cost) values (4, 'Bed', 1, 100);
INSERT INTO Material (project_id, material_name, num_required, cost) values (4, 'Sheet Set', 1, 20);

INSERT INTO step (project_id, step_text, step_order) values (4, 'Make the bed with the sheets', 1);

INSERT INTO project_category (project_id, category_id) values (4, 2);

-- Tie shoes
insert into project (project_name, estimated_hours, actual_hours, difficulty, notes) values ('Tie Shoes', 0.02, 0.02, 1, 'look up a video on how to do this if you do not already know how.');

INSERT INTO material (project_id, material_name, num_required, cost) values (5, 'Shoes', 1, 100);

INSERT INTO step (project_id, step_text, step_order) values (5, 'Put on the shoes and ties the laces', 1);

INSERT INTO project_category (project_id, category_id) values (5, 3);

-- study
insert into project (project_name, estimated_hours, actual_hours, difficulty, notes) values ('Homework', 2, 2, 2, 'You should do this for a minimum 2 hours a day');

INSERT INTO material (project_id, material_name, num_required, cost) values (6, 'Book', 1, 100);
INSERT INTO material (project_id, material_name, num_required, cost) values (6, 'Paper', 1, 5);
INSERT INTO material (project_id, material_name, num_required, cost) values (6, 'Pencil', 1, 1);

INSERT INTO step (project_id, step_text, step_order) values (6, 'Using materials do your homework that the teacher gave you', 1);

INSERT INTO project_category (project_id, category_id) values (6, 4);