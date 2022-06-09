package com.microservices.schemaservice

const val rule2 = "package rules\n" +
        "import com.microservices.shared.SensorReadingMapped\n" +
        "\n" +
        "declare SensorReadingMapped\n" +
        "    @role(event)\n" +
        "end\n" +
        "\n" +
        "declare window LastReadings\n" +
        "    SensorReadingMapped() over window:length(5) from entry-point \"ReadingStream\"\n" +
        "end\n" +
        "\n" +
        "rule \"rule2\"\n" +
        "    no-loop true\n" +
        "    lock-on-active true\n" +
        "    salience 1\n" +
        "    when\n" +
        "        Number(intValue > 3) from accumulate(\n" +
        "                \$r : SensorReadingMapped(content[\"value10\"] == 6) from window LastReadings,\n" +
        "                count(\$r))\n" +
        "    then\n" +
        "        System.out.println(\"test\");\n" +
        "end"

const val rule1 = "package rules\n" +
        "import com.microservices.shared.SensorReadingMapped\n" +
        "\n" +
        "declare SensorReadingMapped\n" +
        "    @role(event)\n" +
        "end\n" +
        "\n" +
        "declare window LastReadings\n" +
        "    SensorReadingMapped() over window:length(5) from entry-point \"ReadingStream\"\n" +
        "end\n" +
        "\n" +
        "rule \"rule1\"\n" +
        "    no-loop true\n" +
        "    lock-on-active true\n" +
        "    salience 1\n" +
        "    when\n" +
        "        Number(intValue > 3) from accumulate(\n" +
        "                \$r : SensorReadingMapped(content[\"valueOne\"] == 3) from window LastReadings,\n" +
        "                count(\$r))\n" +
        "    then\n" +
        "        System.out.println(\"XDDDDDDDDDDDDD\");\n" +
        "end"

const val rule3 = "package rules\n" +
        "import com.microservices.shared.SensorReadingMapped\n" +
        "\n" +
        "declare SensorReadingMapped\n" +
        "    @role(event)\n" +
        "end\n" +
        "\n" +
        "declare window LastReadings\n" +
        "    SensorReadingMapped() over window:length(5) from entry-point \"ReadingStream\"\n" +
        "end\n" +
        "\n" +
        "rule \"rule1\"\n" +
        "    no-loop true\n" +
        "    lock-on-active true\n" +
        "    salience 1\n" +
        "    when\n" +
        "        Number(intValue > 2) from accumulate(\n" +
        "                \$r : SensorReadingMapped(content[\"valueOne\"] == 3) from window LastReadings,\n" +
        "                count(\$r))\n" +
        "    then\n" +
        "        System.out.println(\"CIOTA2\");\n" +
        "end"

const val rule4 = "package rules\n" +
        "import com.microservices.shared.SensorReadingMapped\n" +
        "\n" +
        "declare SensorReadingMapped\n" +
        "    @role(event)\n" +
        "end\n" +
        "\n" +
        "declare window LastReadings\n" +
        "    SensorReadingMapped() over window:length(5) from entry-point \"ReadingStream\"\n" +
        "end\n" +
        "\n" +
        "rule \"rule2\"\n" +
        "    no-loop true\n" +
        "    lock-on-active true\n" +
        "    salience 1\n" +
        "    when\n" +
        "        Number(intValue > 1) from accumulate(\n" +
        "                \$r : SensorReadingMapped(content[\"value9\"] == \"hehehxde\") from window LastReadings,\n" +
        "                count(\$r))\n" +
        "    then\n" +
        "        System.out.println(\"elegancja francja\");\n" +
        "end"